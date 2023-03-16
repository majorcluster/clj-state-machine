(ns clj-state-machine.models.transition
  (:require [clj-state-machine.models.status :as models.status]
            [schema.core :as s]))

(def transition-skeleton
  {:transition/id s/Uuid
   (s/optional-key :transition/name) s/Str
   (s/optional-key :transition/status-from) models.status/StatusDef
   :transition/status-to models.status/StatusDef})

(s/defschema TransitionDef transition-skeleton)

(def transition-input-skeleton
  {(s/optional-key :transition/id) s/Uuid
   :transition/name s/Str
   (s/optional-key :transition/status-from) models.status/StatusDef
   :transition/status-to models.status/StatusDef})

(s/defschema TransitionInputDef transition-input-skeleton)

(def transition-loose-input-skeleton
  {:transition/id s/Uuid
   (s/optional-key :transition/name) s/Str
   (s/optional-key :transition/status-from) models.status/StatusDef
   (s/optional-key :transition/status-to) models.status/StatusDef})

(s/defschema TransitionLooseInputDef transition-loose-input-skeleton)
