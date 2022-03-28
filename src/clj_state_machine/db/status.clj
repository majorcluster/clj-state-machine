(ns clj-state-machine.db.status
  (:require [clj-state-machine.db.config :as db.config]
            [clj-state-machine.model.utils :as m.utils]
            [clj-state-machine.db.entity :as db.entity])
  (:use clojure.pprint))

(defn upsert!
  [status]
  (let [id (m.utils/uuid)
        status-complete (assoc status :status/id id)
        lookup-ref [:status/id id]]
    (db.entity/upsert! lookup-ref status-complete)))