(ns clj-state-machine.wire.datomic.workflow
  (:require [clj-state-machine.wire.datomic.transition :as datomic.transition]
            [schema.core :as s]))

(def workflow-skeleton
  {:workflow/id s/Uuid
   (s/optional-key :workflow/name) s/Str
   (s/optional-key :workflow/transitions) [datomic.transition/TransitionDef]})

(s/defschema WorkflowDef workflow-skeleton)
