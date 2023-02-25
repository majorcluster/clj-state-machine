(ns clj-state-machine.wire.datomic.status
  (:require [schema.core :as s]))

(def status-skeleton
  {:status/id s/Uuid
   (s/optional-key :status/name) s/Str})

(s/defschema StatusDef status-skeleton)
