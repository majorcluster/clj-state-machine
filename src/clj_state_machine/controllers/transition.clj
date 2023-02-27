(ns clj-state-machine.controllers.transition
  (:require [clj-state-machine.models.transition :as models.transition]
            [clj-state-machine.ports.datomic.transition :as datomic.transition]
            [schema.core :as s]))

(s/defn get-facade :- (s/cond-pre [models.transition/TransitionDef] models.transition/TransitionDef)
  [id :- (s/maybe s/Uuid)]
  (let [no-id? (nil? id)]
    (cond no-id? (datomic.transition/find-all)
          :else (datomic.transition/find-one id))))

(s/defn upsert-facade :- s/Uuid
  [transition :- models.transition/TransitionInputDef
   workflow-id :- s/Uuid]
  (datomic.transition/upsert! workflow-id transition))

(s/defn delete-facade
  [_ :- s/Keyword
   id :- s/Uuid]
  (datomic.transition/delete! id))
