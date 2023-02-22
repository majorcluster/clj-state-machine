(ns clj-state-machine.wire.http-in.transition
  (:require [schema.core :as s]))

(defn TransitionDef
  []
  {:transition/id s/Uuid
   (s/optional-key :transition/name) s/Str})
