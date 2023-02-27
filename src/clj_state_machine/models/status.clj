(ns clj-state-machine.models.status
  (:require [schema.core :as s]))

(def status-skeleton
  {:status/id s/Uuid
   (s/optional-key :status/name) s/Str})

(s/defschema StatusDef status-skeleton)

(def status-input-skeleton
  {(s/optional-key :status/id) s/Uuid
   :status/name s/Str})

(s/defschema StatusInputDef status-input-skeleton)
