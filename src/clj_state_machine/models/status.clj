(ns clj-state-machine.models.status
  (:require [schema.core :as s]))

(defn StatusDef
  []
  {:status/id s/Uuid
   (s/optional-key :status/name) s/Str})
