(ns clj-state-machine.db.workflow
  (:require [clj-state-machine.db.config :as db.config]
            [clj-state-machine.db.entity :as db.entity]
            [pedestal-api-helper.params-helper :as p-helper]
            [datomic.api :as d])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(defn upsert!
  [workflow]
  (let [id (:workflow/id workflow)
        id (cond id id
                 :else (p-helper/uuid))
        workflow-complete (assoc workflow :workflow/id id)
        lookup-ref [:workflow/id id]]
    (db.entity/upsert! lookup-ref workflow-complete)
    id))

(defn delete!
  [id]
  (let [conn (db.config/connect!)
        id (cond (p-helper/is-uuid id) (UUID/fromString id)
                 :else id)
        lookup-ref [:workflow/id id]]
    (d/transact conn
                [[:db/retractEntity lookup-ref]])))
