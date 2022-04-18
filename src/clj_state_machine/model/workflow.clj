(ns clj-state-machine.model.workflow
  (:require [schema.core :as s]))

(defn WorkflowDef
  []
  {:workflow/id s/Uuid
   (s/optional-key :workflow/name) s/Str})
