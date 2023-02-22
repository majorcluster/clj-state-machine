(ns clj-state-machine.wire.http-in.workflow
  (:require [clj-state-machine.wire.http-in.transition :as m.t]
            [schema.core :as s]))

(defn WorkflowDef
  []
  {:workflow/id s/Uuid
   (s/optional-key :workflow/name) s/Str
   (s/optional-key :workflow/transitions) [(m.t/TransitionDef)]})
