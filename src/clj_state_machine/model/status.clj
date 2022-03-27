(ns clj-state-machine.model.status
  (:require [schema.core :as s]))

(defn StatusDef
  []
  {:status/id s/Uuid
   (s/optional-key :status/name) s/Str})

(defn StatusPostDef
  []
  {:status/name s/Str})
