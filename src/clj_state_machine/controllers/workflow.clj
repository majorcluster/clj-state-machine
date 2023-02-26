(ns clj-state-machine.controllers.workflow
  (:require [clj-state-machine.models.workflow :as models.workflow]
            [clj-state-machine.ports.datomic.workflow :as datomic.workflow]
            [schema.core :as s]))

(s/defn get-facade :- (s/cond-pre [models.workflow/WorkflowDef] models.workflow/WorkflowDef)
  [id :- (s/maybe s/Uuid)]
  (let [no-id? (nil? id)]
    (cond no-id? (datomic.workflow/find-all)
          :else (datomic.workflow/find-one id))))

(s/defn upsert-facade :- s/Uuid
  [workflow :- models.workflow/WorkflowInputDef]
  (datomic.workflow/upsert! workflow))

(s/defn delete-facade
  [_ :- s/Keyword
   id :- s/Uuid]
  (datomic.workflow/delete! id))
