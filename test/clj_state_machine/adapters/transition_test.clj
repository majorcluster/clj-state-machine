(ns clj-state-machine.adapters.transition-test
  (:require [clj-state-machine.adapters.transition :as adapters.transition]
            [clojure.test :refer :all]
            [schema.test :as st])
  (:import [java.util UUID]))

(st/deftest post-wire->internal-test
  (let [status-opened-id (UUID/randomUUID)
        status-closed-id (UUID/randomUUID)]
    (testing "conversion is done"
      (are [internal wire] (= internal (adapters.transition/post-wire->internal wire))
        {:transition/name "open"
         :transition/status-to {:status/id status-opened-id}} {:name "open" :status-to status-opened-id}
        {:transition/name "open"
         :transition/status-from {:status/id status-closed-id}
         :transition/status-to {:status/id status-opened-id}} {:name "open" :status-from status-closed-id :status-to status-opened-id}))))

(st/deftest patch-wire->internal-test
  (let [transition-id (UUID/randomUUID)
        status-opened-id (UUID/randomUUID)
        status-closed-id (UUID/randomUUID)]
    (testing "conversion is done"
      (are [internal wire] (= internal (adapters.transition/patch-wire->internal wire))
        {:transition/id transition-id} {:id transition-id}
        {:transition/id transition-id
         :transition/name "open"
         :transition/status-to {:status/id status-opened-id}} {:id transition-id :name "open" :status-to status-opened-id}
        {:transition/id transition-id
         :transition/name "open"
         :transition/status-from {:status/id status-closed-id}
         :transition/status-to {:status/id status-opened-id}} {:id transition-id :name "open" :status-from status-closed-id :status-to status-opened-id}))))

(st/deftest internal->wire-test
  (let [transition-id (UUID/randomUUID)
        status-opened-id (UUID/randomUUID)
        status-closed-id (UUID/randomUUID)
        status-opened {:id (str status-opened-id)
                       :name "opened"}
        status-closed {:id (str status-closed-id)
                       :name "closed"}
        internal-status-opened {:status/id status-opened-id
                                :status/name "opened"}
        internal-status-closed {:status/id status-closed-id
                                :status/name "closed"}]
    (testing "conversion is done"
      (are [wire internal] (= wire (adapters.transition/internal->wire internal))
        {:id (str transition-id)
         :name "open" :status-to status-opened} {:transition/id transition-id :transition/name "open"
                                                 :transition/status-to internal-status-opened}
        [{:id (str transition-id)
          :name "open" :status-to status-opened}
         {:id (str transition-id)
          :name "open" :status-to status-opened}] [{:transition/id transition-id :transition/name "open"
                                                    :transition/status-to internal-status-opened}
                                                   {:transition/id transition-id :transition/name "open"
                                                    :transition/status-to internal-status-opened}]
        {:id (str transition-id) :name "open"
         :status-from status-closed :status-to status-opened} {:transition/id transition-id :transition/name "open"
                                                               :transition/status-from internal-status-closed
                                                               :transition/status-to internal-status-opened}))))
