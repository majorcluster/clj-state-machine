(ns clj-state-machine.models.transition
  (:require [schema.core :as s]))

(def transition-skeleton
  {:transition/id s/Uuid
   (s/optional-key :transition/name) s/Str})

(s/defschema TransitionDef transition-skeleton)
