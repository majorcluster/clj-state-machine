(ns clj-state-machine.ports.datomic.status
  (:require [clj-state-machine.ports.datomic.core :as datomic.core]
            [datomic-helper.entity :as dh.entity]
            [datomic.api :as d]
            [pedestal-api-helper.params-helper :as p-helper])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(defn upsert!
  [status]
  (let [conn (datomic.core/connect!)
        id (:status/id status)
        id (cond id id
                 :else (p-helper/uuid))
        status-complete (assoc status :status/id id)
        lookup-ref [:status/id id]]
    (dh.entity/upsert! conn lookup-ref status-complete)
    id))

(defn delete!
  [id]
  (let [conn (datomic.core/connect!)
        id (cond (p-helper/is-uuid id) (UUID/fromString id)
                 :else id)
        lookup-ref [:status/id id]]
    (d/transact conn
                [[:db/retractEntity lookup-ref]])))
