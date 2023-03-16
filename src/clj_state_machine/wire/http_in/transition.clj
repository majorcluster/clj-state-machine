(ns clj-state-machine.wire.http-in.transition
  (:require [clj-state-machine.wire.http-in.status :as in.status]
            [schema.core :as s]))

(def transition-skeleton
  {:id  s/Str
   :name s/Str
   (s/optional-key :status-from) in.status/StatusDef
   :status-to in.status/StatusDef})

(s/defschema TransitionDef transition-skeleton)

(def transition-post-skeleton
  {:id  s/Uuid
   :name s/Str
   (s/optional-key :status-from) s/Uuid
   :status-to s/Uuid})

(s/defschema TransitionPostInputDef (-> transition-post-skeleton
                                        (dissoc :id)))

(def transition-patch-skeleton
  {:id  s/Uuid
   (s/optional-key :name) s/Str
   (s/optional-key :status-from) s/Uuid
   (s/optional-key :status-to) s/Uuid})

(s/defschema TransitionPatchInputDef transition-patch-skeleton)

(def get-transition-payload-skeleton
  (s/cond-pre [TransitionDef] TransitionDef))

(s/defschema GetTransitionPayloadDef get-transition-payload-skeleton)

(def post-put-transition-payload-skeleton
  {:id s/Uuid})

(s/defschema PostPutTransitionPayloadDef post-put-transition-payload-skeleton)
