(ns clj-state-machine.ports.datomic.workflow
  (:require [clj-state-machine.ports.datomic.core :as datomic.core]
            [datomic-helper.entity :as dh.entity]
            [datomic.api :as d]
            [pedestal-api-helper.params-helper :as p-helper])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(defn upsert!
  [workflow]
  (let [conn (datomic.core/connect!)
        id (:workflow/id workflow)
        id (cond id id
                 :else (p-helper/uuid))
        workflow-complete (assoc workflow :workflow/id id)
        lookup-ref [:workflow/id id]]
    (dh.entity/upsert! conn lookup-ref workflow-complete)
    id))

(defn delete!
  [id]
  (let [conn (datomic.core/connect!)
        id (cond (p-helper/is-uuid id) (UUID/fromString id)
                 :else id)
        lookup-ref [:workflow/id id]]
    (d/transact conn
                [[:db/retractEntity lookup-ref]])))
