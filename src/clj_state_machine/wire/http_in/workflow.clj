(ns clj-state-machine.wire.http-in.workflow
  (:require [clj-state-machine.wire.http-in.transition :as m.t]
            [schema.core :as s]))

(def workflow-skeleton
  {:id s/Uuid
   (s/optional-key :name) s/Str
   (s/optional-key :transitions) [(m.t/TransitionDef)]})

(s/defschema WorkflowDef workflow-skeleton)

(def get-workflow-payload-skeleton
  (s/cond-pre [WorkflowDef] WorkflowDef))

(s/defschema GetWorkflowPayloadDef get-workflow-payload-skeleton)

(def post-put-workflow-payload-skeleton
  {:id s/Uuid})

(s/defschema PostPutWorkflowPayloadDef post-put-workflow-payload-skeleton)
