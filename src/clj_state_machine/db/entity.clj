(ns clj-state-machine.db.entity
  (:require [clj-state-machine.db.config :as db.config]
            [clojure.walk :as walk]
            [datomic.api :as d]
            [clojure.set :as cset])
  (:use clojure.pprint))

(defn transform-nested-out
  [result]
  (if (map? result)
    (dissoc result :db/id)
    result))

(defn transform-out
  [result-seq]
  (let [transformed (walk/prewalk transform-nested-out result-seq)]
    (if (empty? transformed)
      nil
      transformed)))

(defn find-by-id
  [conn id-ks id]
  (println "id" id)
  (let [db (d/db conn)
        q '[:find (pull ?e [*]) .
            :in $ ?id-ks ?id
            :where [?e ?id-ks ?id]]]
    (->> id
          (d/q q db id-ks)
          (transform-out))))

(defn update!
        [conn
         id-ks
         id
         found-entity
         to-be-saved]
        (let [attr-old (set (keys found-entity))
              attr-partial (set (keys to-be-saved))
              intersect (disj (cset/intersection attr-old attr-partial) id-ks)
              txs (map (fn [attr]
                         [:db/cas
                          [id-ks id]
                          attr
                          (get found-entity attr)
                          (get to-be-saved attr)])
                       intersect)]
          (d/transact conn txs)))

(defn insert!
  [conn
   to-be-saved]
  (d/transact conn [to-be-saved]))

(defn upsert!
  [[id-ks id] to-be-saved]
  (let [conn (db.config/connect!)
        found-entity (find-by-id conn id-ks id)]
    (cond found-entity
          (update! conn id-ks id found-entity to-be-saved)
          :else (insert! conn to-be-saved))))
