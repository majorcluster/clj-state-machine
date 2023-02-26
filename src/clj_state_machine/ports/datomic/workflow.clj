(ns clj-state-machine.ports.datomic.workflow
  (:require [clj-state-machine.models.workflow :as models.workflow]
            [clj-state-machine.ports.datomic.core :as datomic.core]
            [datomic-helper.entity :as dh.entity]
            [datomic.api :as d]
            [pedestal-api-helper.params-helper :as p-helper]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn upsert! :- s/Uuid
  [workflow :- models.workflow/WorkflowInputDef]
  (let [conn (datomic.core/connect!)
        id (:workflow/id workflow)
        id (cond id id
                 :else (p-helper/uuid))
        workflow-complete (assoc workflow :workflow/id id)
        lookup-ref [:workflow/id id]]
    (dh.entity/upsert! conn lookup-ref workflow-complete)
    id))

(s/defn delete!
  [id :- s/Uuid]
  (let [conn (datomic.core/connect!)
        id (cond (p-helper/is-uuid id) (UUID/fromString id)
                 :else id)
        lookup-ref [:workflow/id id]]
    (d/transact conn
                [[:db/retractEntity lookup-ref]])))

(s/defn find-one :- (s/maybe models.workflow/WorkflowDef)
  [id :- s/Uuid]
  (->> id
       (dh.entity/find-by-id (datomic.core/connect!) :workflow/id)))

(s/defn find-all :- [models.workflow/WorkflowDef]
  []
  (->> :workflow/id
       (dh.entity/find-all (datomic.core/connect!))))
