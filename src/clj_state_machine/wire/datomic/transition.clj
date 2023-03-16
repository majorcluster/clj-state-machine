(ns clj-state-machine.wire.datomic.transition
  (:require [clj-state-machine.wire.datomic.status :as datomic.status]
            [schema.core :as s]))

(def transition-skeleton
  {:transition/id s/Uuid
   (s/optional-key :transition/name) s/Str
   (s/optional-key :transition/status-from) datomic.status/StatusDef
   :transition/status-to datomic.status/StatusDef})

(s/defschema TransitionDef transition-skeleton)
