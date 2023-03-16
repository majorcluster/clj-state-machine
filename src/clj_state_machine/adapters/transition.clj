(ns clj-state-machine.adapters.transition
  (:require [clj-data-adapter.core :as data-adapter]
            [clj-state-machine.models.transition :as models.transition]
            [clj-state-machine.wire.http-in.transition :as in.transition]
            [schema.core :as s]))

(s/defn post-wire->internal :- models.transition/TransitionInputDef
  [wire :- in.transition/TransitionPostInputDef]
  (let [transformed (data-adapter/transform {:transition/name :name
                                             :transition/status-from {:status/id (data-adapter/opt :status-from)}
                                             :transition/status-to   {:status/id :status-to}}
                                            wire
                                            {:data-adapter-transform/clear-empty true})]
    transformed))

(s/defn patch-wire->internal :- models.transition/TransitionLooseInputDef
  [wire :- in.transition/TransitionPatchInputDef]
  (let [transformed (data-adapter/transform {:transition/id :id
                                             :transition/name (data-adapter/opt :name)
                                             :transition/status-from {:status/id (data-adapter/opt :status-from)}
                                             :transition/status-to   {:status/id (data-adapter/opt :status-to)}}
                                            wire
                                            {:data-adapter-transform/clear-empty true})]
    transformed))

(s/defn internal->wire :- (s/cond-pre [in.transition/GetTransitionPayloadDef] in.transition/GetTransitionPayloadDef)
  [internal :- (s/cond-pre [models.transition/TransitionDef] models.transition/TransitionDef)]
  (let [transformed (->> internal
                         (data-adapter/transform-keys data-adapter/namespaced-key->kebab-key)
                         (data-adapter/transform-values data-adapter/uuid->str))]
    transformed))
