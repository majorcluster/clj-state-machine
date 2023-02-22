(ns clj-state-machine.wire.datomic.transition
  (:require [schema.core :as s]))

(defn TransitionDef
  []
  {:transition/id s/Uuid
   (s/optional-key :transition/name) s/Str})
