(ns clj-state-machine.db.status
  (:require [clj-state-machine.db.config :as db.config]
            [pedestal-api-helper.params-helper :as p-helper]
            [datomic-helper.entity :as dh.entity]
            [datomic.api :as d])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(defn upsert!
  [status]
  (let [conn (db.config/connect!)
        id (:status/id status)
        id (cond id id
                 :else (p-helper/uuid))
        status-complete (assoc status :status/id id)
        lookup-ref [:status/id id]]
    (dh.entity/upsert! conn lookup-ref status-complete)
    id))

(defn delete!
  [id]
  (let [conn (db.config/connect!)
        id (cond (p-helper/is-uuid id) (UUID/fromString id)
                 :else id)
        lookup-ref [:status/id id]]
    (d/transact conn
                [[:db/retractEntity lookup-ref]])))