(ns clj-state-machine.wire.http-in.status
  (:require [schema.core :as s]))

(def status-skeleton
  {:id s/Uuid
   (s/optional-key :name) s/Str})

(s/defschema StatusDef status-skeleton)

(def get-status-payload-skeleton
  (s/cond-pre [StatusDef] StatusDef))

(s/defschema GetStatusPayloadDef get-status-payload-skeleton)
