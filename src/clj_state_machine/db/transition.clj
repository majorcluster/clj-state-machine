(ns clj-state-machine.db.transition
  (:require [clj-state-machine.db.config :as db.config]
            [pedestal-api-helper.params-helper :as p-helper]
            [datomic-helper.entity :as dh.entity]
            [datomic.api :as d])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(defn upsert!
  [workflow-id entity]
  (let [conn (db.config/connect!)
        id (:transition/id entity)
        id (cond id id
                 :else (p-helper/uuid))
        complete (assoc entity :transition/id id)]
    (dh.entity/upsert-foreign!
      conn
      [:transition/id id]
      :workflow/id
      workflow-id
      :workflow/transitions complete)
    id))

(defn delete!
  [id]
  (let [conn (db.config/connect!)
        id (cond (p-helper/is-uuid id) (UUID/fromString id)
                 :else id)
        lookup-ref [:transition/id id]]
    (d/transact conn
                [[:db/retractEntity lookup-ref]])))
