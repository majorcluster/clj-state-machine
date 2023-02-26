(ns clj-state-machine.wire.datomic.transition
  (:require [schema.core :as s]))

(def transition-skeleton
  {:transition/id s/Uuid
   (s/optional-key :transition/name) s/Str})

(s/defschema TransitionDef transition-skeleton)
