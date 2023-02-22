(ns clj-state-machine.ports.datomic.schema
  (:require [clj-state-machine.wire.datomic.status :as datomic.status]
            [clj-state-machine.wire.datomic.transition :as datomic.transition]
            [clj-state-machine.wire.datomic.workflow :as datomic.workflow]
            [datomic-helper.schema-transform :as dh.s-transform]))

(defonce specs
         (dh.s-transform/schemas-to-datomic [(datomic.status/StatusDef)
                                             (datomic.transition/TransitionDef)
                                             (datomic.workflow/WorkflowDef)]
                                            {:components [:workflow/transitions]}))
