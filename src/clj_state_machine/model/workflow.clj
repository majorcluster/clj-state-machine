(ns clj-state-machine.model.workflow
  (:require [schema.core :as s]
            [clj-state-machine.model.transition :as m.t]))

(defn WorkflowDef
  []
  {:workflow/id s/Uuid
   (s/optional-key :workflow/name) s/Str
   (s/optional-key :workflow/transitions) [(m.t/TransitionDef)]})
