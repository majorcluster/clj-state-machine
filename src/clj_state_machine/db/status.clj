(ns clj-state-machine.db.status
  (:require [clj-state-machine.db.config :as db.config]
            [clj-state-machine.model.utils :as m.utils]
            [clj-state-machine.db.entity :as db.entity]
            [datomic.api :as d])
  (:use clojure.pprint))

(defn upsert!
  [status]
  (let [id (:status/id status)
        id (cond id id
                 :else (m.utils/uuid))
        status-complete (assoc status :status/id id)
        lookup-ref [:status/id id]]
    (db.entity/upsert! lookup-ref status-complete)
    id))

(defn delete!
  [id]
  (let [conn (db.config/connect!)
        lookup-ref [:status/id id]]
    (d/transact conn
                [[:db/retractEntity lookup-ref]])))