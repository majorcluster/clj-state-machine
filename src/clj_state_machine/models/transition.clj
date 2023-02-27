(ns clj-state-machine.models.transition
  (:require [schema.core :as s]))

(def transition-skeleton
  {:transition/id s/Uuid
   (s/optional-key :transition/name) s/Str})

(s/defschema TransitionDef transition-skeleton)

(def transition-input-skeleton
  {(s/optional-key :transition/id) s/Uuid
   :transition/name s/Str})

(s/defschema TransitionInputDef transition-input-skeleton)
