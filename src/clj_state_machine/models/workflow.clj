(ns clj-state-machine.models.workflow
  (:require [clj-state-machine.models.transition :as models.transition]
            [schema.core :as s]))

(def workflow-skeleton
  {:workflow/id s/Uuid
   (s/optional-key :workflow/name) s/Str
   (s/optional-key :workflow/transitions) [(models.transition/TransitionDef)]})

(s/defschema WorkflowDef workflow-skeleton)

(def workflow-input-skeleton
  {(s/optional-key :workflow/id) s/Uuid
   :workflow/name s/Str})

(s/defschema WorkflowInputDef workflow-input-skeleton)
