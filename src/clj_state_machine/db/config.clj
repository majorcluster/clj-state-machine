(ns clj-state-machine.db.config
  (:require [datomic.api :as d]
            [clj-state-machine.db.utils :as db.utils]
            [clj-state-machine.model.status :as model.status])
  (:use clojure.pprint))

;TODO extract to some env variables
(defn db-uri
  []
  "datomic:dev://localhost:4334/clj-state-machine-local")

(defn connect! []
  (d/create-database (db-uri))
  (d/connect (db-uri)))

(defn get-schema []
  (println "GET SCHEMA")
  (pprint (reduce conj [] (db.utils/schema-to-datomic (model.status/StatusDef))))
  (reduce conj [] (db.utils/schema-to-datomic (model.status/StatusDef))))

(defn create-schema!
  [conn]
  (d/transact conn (get-schema)))

(defn erase-db!
  "tet use only!!!"
  []
  (d/delete-database db-uri))

(defn reset-db!
  "test use only!!!"
  []
  (erase-db!)
  (let [conn (connect!)]
    (create-schema! conn)
    conn))

(defn start-db
  []
  (let [conn (connect!)]
    (create-schema! conn)))