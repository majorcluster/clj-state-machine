(ns clj-state-machine.wire.http-in.transition
  (:require [schema.core :as s]))

(def transition-skeleton
  {:id s/Uuid
   (s/optional-key :name) s/Str})

(s/defschema TransitionDef transition-skeleton)

(def get-transition-payload-skeleton
  (s/cond-pre [TransitionDef] TransitionDef))

(s/defschema GetTransitionPayloadDef get-transition-payload-skeleton)

(def post-put-transition-payload-skeleton
  {:id s/Uuid})

(s/defschema PostPutTransitionPayloadDef post-put-transition-payload-skeleton)
