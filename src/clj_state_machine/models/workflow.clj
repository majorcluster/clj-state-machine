(ns clj-state-machine.models.workflow
  (:require [clj-state-machine.models.transition :as m.t]
            [schema.core :as s]))

(defn WorkflowDef
  []
  {:workflow/id s/Uuid
   (s/optional-key :workflow/name) s/Str
   (s/optional-key :workflow/transitions) [(m.t/TransitionDef)]})
