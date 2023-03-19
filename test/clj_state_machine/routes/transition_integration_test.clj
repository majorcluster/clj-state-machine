(ns clj-state-machine.routes.transition-integration-test
  (:require [clj-data-adapter.core :as data-adapter]
            [clj-state-machine.ports.datomic.core :as datomic.core]
            [clj-state-machine.ports.datomic.status :as datomic.status]
            [clj-state-machine.ports.datomic.transition :as datomic.transition]
            [clj-state-machine.ports.datomic.workflow :as datomic.workflow]
            [clojure.data.json :as json]
            [clojure.test :refer :all]
            [core-test :refer [base-message id-as-string json-as-map
                               json-header map-as-json service test-fixture]]
            [datomic-helper.entity :as dh.entity]
            [io.pedestal.test :as p.test]
            [pedestal-api-helper.params-helper :as p-helper]
            [schema.test :as st])
  (:import (java.util UUID)))

(use-fixtures :each test-fixture)

(def workflow-id (p-helper/uuid))
(def workflow-2-id (p-helper/uuid))

(def workflow-present-datomic
  {:workflow/id workflow-id
   :workflow/name "checkout"})

(def workflow-2-present-datomic
  {:workflow/id workflow-2-id
   :workflow/name "another-wf"})

(defn workflow-present-url-piece []
  (str "/workflow/" workflow-id))

(def status-open
  {:status/id (p-helper/uuid)
   :status/name "open"})

(def status-closed
  {:status/id (p-helper/uuid)
   :status/name "closed"})

(def transition-open-datomic
  {:transition/id (p-helper/uuid)
   :transition/name "open"
   :transition/status-to status-open})

(def transition-close-datomic
  {:transition/id (p-helper/uuid)
   :transition/name "close"
   :transition/status-from status-open
   :transition/status-to status-closed})

(def transition-to-update-datomic
  {:transition/id (p-helper/uuid)
   :transition/name "express-checkout"
   :transition/status-from status-open
   :transition/status-to status-open})

(def transition-mobile-checkout-present-datomic
  {:transition/id (p-helper/uuid)
   :transition/name "mobile-checkout"
   :transition/status-to status-open})

(def transition-to-delete-datomic
  {:transition/id (p-helper/uuid)
   :transition/name "delete-me"
   :transition/status-to status-open})

(defn- transition-present-view
  ([transition status-from status-to]
   (assoc (transition-present-view transition status-to)
     :status-from {:id (:status/id status-from)
                   :name (:status/name status-from)}))
  ([transition status-to]
   {:id (get transition :transition/id)
    :name (get transition :transition/name)
    :status-to {:id (:status/id status-to)
                :name (:status/name status-to)}})
  ([]
   {:id (get transition-open-datomic :transition/id)
    :name (get transition-open-datomic :transition/name)
    :status-to {:id (:status/id status-open)
                :name (:status/name status-open)}}))

(defn- transition-present-post-input
  []
  {:id (get transition-open-datomic :transition/id)
   :name (get transition-open-datomic :transition/name)
   :status-to (str (:status/id status-open))})

(defn- all-transitions-view
  []
  (->> [transition-open-datomic
        transition-close-datomic
        transition-mobile-checkout-present-datomic]
       (data-adapter/transform-values data-adapter/uuid->str)
       (data-adapter/transform-keys data-adapter/namespaced-key->kebab-key)
       set))

(def transition-to-update-view
  {:id (get transition-to-update-datomic :transition/id)
   :name (get transition-to-update-datomic :transition/name)
   :status-from (get transition-to-update-datomic :transition/status-from)
   :status-to (get transition-to-update-datomic :transition/status-to)})

(defn- transition-to-delete-view
  []
  {:id (get transition-to-delete-datomic :transition/id)
   :name (get transition-to-delete-datomic :transition/name)})

(defn- insert-test-data []
  (datomic.status/upsert! status-open)
  (datomic.status/upsert! status-closed)
  (datomic.workflow/upsert! workflow-present-datomic)
  (datomic.transition/upsert! workflow-id transition-open-datomic)
  (datomic.transition/upsert! workflow-id transition-close-datomic)
  (datomic.transition/upsert! workflow-id transition-mobile-checkout-present-datomic))

(st/deftest get-transition-test
  (insert-test-data)
  (testing "Already present transition is gotten"
    (let [expected-resp (assoc base-message :payload (transition-present-view))
          transition-present-get-url (str "/transition/"
                                          (id-as-string
                                           transition-open-datomic
                                           :transition/id))
          actual-resp (p.test/response-for service :get transition-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 200 (:status actual-resp)))))

  (testing "non uuid gives not found"
    (let [expected-resp {:message "transition was not found with given id"}
          transition-present-get-url "/transition/39384"
          actual-resp (p.test/response-for service :get transition-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp)))))

  (testing "not present uuid gives not found"
    (let [expected-resp {:message "transition was not found with given id"}
          transition-present-get-url (str "/transition/" (p-helper/uuid))
          actual-resp (p.test/response-for service :get transition-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp)))))

  (testing "When no id is sent all transitions are returned"
    (let [actual-resp (p.test/response-for service :get "/transition")
          body-map (->> actual-resp
                        :body
                        (json-as-map)
                        :payload)]
      (is (= (all-transitions-view) (set body-map)))
      (is (= 200 (:status actual-resp))))))

(st/deftest get-workflow-transition-test
  (datomic.workflow/upsert! workflow-present-datomic)
  (datomic.workflow/upsert! workflow-2-present-datomic)
  (datomic.transition/upsert! workflow-id transition-open-datomic)
  (datomic.transition/upsert! workflow-2-id transition-mobile-checkout-present-datomic)
  (testing "Already present transition is gotten"
    (let [expected-resp (assoc base-message :payload [(transition-present-view)])
          url (str "/workflow/" workflow-id "/transition")
          actual-resp (p.test/response-for service :get url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 200 (:status actual-resp)))))

  (testing "non uuid gives not found"
    (let [expected-resp {:message "transition was not found with given id"}
          url "/workflow/39384/transition"
          actual-resp (p.test/response-for service :get url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp)))))

  (testing "not present uuid gives empty"
    (let [expected-resp (assoc base-message :payload [])
          url (str "/workflow/" (p-helper/uuid) "/transition")
          actual-resp (p.test/response-for service :get url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 200 (:status actual-resp))))))

(st/deftest get-status-from-transition-test
  (datomic.workflow/upsert! workflow-present-datomic)
  (datomic.workflow/upsert! workflow-2-present-datomic)
  (datomic.transition/upsert! workflow-2-id (assoc transition-mobile-checkout-present-datomic
                                              :transition/id (p-helper/uuid)))
  (datomic.transition/upsert! workflow-id transition-open-datomic)
  (datomic.transition/upsert! workflow-id transition-close-datomic)
  (datomic.transition/upsert! workflow-id transition-mobile-checkout-present-datomic)
  (testing "When no status-from is passed"
    (let [expected-resp (assoc base-message :payload [(transition-present-view transition-open-datomic
                                                                               status-open)
                                                      (transition-present-view transition-mobile-checkout-present-datomic
                                                                               status-open)])
          url (str "/workflow/" workflow-id "/transition/status-from")
          actual-resp (p.test/response-for service :get url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 200 (:status actual-resp)))))

  (testing "When status-from is passed"
    (let [expected-resp (->> [(transition-present-view transition-close-datomic
                                                       status-open
                                                       status-closed)]
                             (assoc base-message :payload))
          url (str "/workflow/" workflow-id "/transition/status-from/" (:status/id status-open))
          actual-resp (p.test/response-for service :get url)
          actual-body (-> actual-resp
                          :body
                          json/read-str)]
      (is (= 200 (:status actual-resp)))
      (is (= (str (get-in expected-resp [:payload 0 :id])) (get-in actual-body ["payload" 0 "id"])))
      (is (= 1 (-> expected-resp :payload count)))
      (is (= (str (get-in expected-resp [:payload 0 :status-from :id]))
             (get-in actual-body ["payload" 0 "status-from" "id"])))))

  (testing "non uuid gives not found"
    (let [expected-resp {:message "transition was not found with given id"}
          url (str "/workflow/" workflow-id "/transition/status-from/39384")
          actual-resp (p.test/response-for service :get url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp)))))

  (testing "non uuid workflow id gives not found"
    (let [expected-resp {:message "transition was not found with given id"}
          url (str "/workflow/39384/transition/status-from/" (p-helper/uuid))
          actual-resp (p.test/response-for service :get url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp)))))

  (testing "not present uuid gives empty"
    (let [expected-resp (assoc base-message :payload [])
          url (str "/workflow/" workflow-id "/transition/status-from/" (p-helper/uuid))
          actual-resp (p.test/response-for service :get url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 200 (:status actual-resp))))))

(st/deftest post-transition-test
  (insert-test-data)
  (testing "insert with mandatory params works"
    (let [new-transition (dissoc (transition-present-post-input) :id)
          new-name "dispatching"
          new-transition (assoc new-transition :name new-name)
          actual-resp (p.test/response-for service
                                           :post (str (workflow-present-url-piece) "/transition")
                                           :headers json-header
                                           :body (map-as-json new-transition))
          body-as-map (json-as-map (:body actual-resp))
          id-gotten (get-in body-as-map [:payload :id] 0)
          id-gotten-as-uuid (UUID/fromString id-gotten)
          conn (datomic.core/connect!)
          transition-in-db (dh.entity/find-by-id conn :transition/id id-gotten-as-uuid)
          workflow-in-db (dh.entity/find-by-id conn :workflow/id (:workflow/id workflow-present-datomic))
          workflow-transitions (get workflow-in-db :workflow/transitions [])
          found-in-workflow (some #(= (:transition/id %) id-gotten-as-uuid) workflow-transitions)]
      (is (not (= 0 id-gotten)))
      (is (= 200 (:status actual-resp)))
      (is (= (:transition/id transition-in-db)
             id-gotten-as-uuid))
      (is found-in-workflow)))
  (testing "insert with missing mandatory params gives 400"
    (let [actual-resp (p.test/response-for service
                                           :post (str (workflow-present-url-piece) "/transition")
                                           :headers json-header
                                           :body "")
          expected-resp {:message "", :validation-messages [{:field "name"
                                                             :message "Field :name is not present"}
                                                            {:field "status-to"
                                                             :message "Field :status-to is not present"}]}]
      (is (= 400 (:status actual-resp)))
      (is (= (map-as-json expected-resp) (:body actual-resp)))))
  (testing "insert with missing workflow-id gives 404"
    (let [actual-resp (p.test/response-for service
                                           :post "/transition"
                                           :headers json-header
                                           :body "")]
      (is (= 404 (:status actual-resp))))))

(st/deftest patch-transition-test
  (insert-test-data)
  (testing "patch with mandatory params and at least 1 extra param works"
    (let [new-name "preparing"
          new-transition (assoc (dissoc transition-to-update-view
                                        :status-from :status-to) :name new-name)
          actual-resp (p.test/response-for service
                                           :patch (str (workflow-present-url-piece) "/transition")
                                           :headers json-header
                                           :body (map-as-json new-transition))
          id (:id transition-to-update-view)
          conn (datomic.core/connect!)
          transition-in-db (dh.entity/find-by-id conn :transition/id id)
          workflow-in-db (dh.entity/find-by-id conn :workflow/id (:workflow/id workflow-present-datomic))
          workflow-transitions (get workflow-in-db :workflow/transitions [])
          found-in-workflow (some #(= (:transition/id %) id) workflow-transitions)]
      (is (= 200 (:status actual-resp)))
      (is (= (:transition/name transition-in-db) new-name))
      (is found-in-workflow)))
  (testing "patch with missing mandatory params gives 400"
    (let [actual-resp (p.test/response-for service
                                           :patch (str (workflow-present-url-piece) "/transition")
                                           :headers json-header
                                           :body "")
          expected-resp {:message "", :validation-messages [{:field "id" :message "Field :id is not present"}]}]
      (is (= 400 (:status actual-resp)))
      (is (= (map-as-json expected-resp) (:body actual-resp)))))
  (testing "patch with missing workflow-id gives 404"
    (let [actual-resp (p.test/response-for service
                                           :patch "/transition"
                                           :headers json-header
                                           :body "")]
      (is (= 404 (:status actual-resp)))))
  (testing "patch with only id gives 400"
    (let [actual-resp (p.test/response-for service
                                           :patch (str (workflow-present-url-piece) "/transition")
                                           :headers json-header
                                           :body (map-as-json (dissoc transition-to-update-view
                                                                      :name :status-from :status-to)))
          expected-resp {:message "At least one of :name,:status-from,:status-to must be present"}]
      (is (= 400 (:status actual-resp)))
      (is (= (map-as-json expected-resp) (:body actual-resp))))))

(st/deftest delete-transition-test
  (insert-test-data)
  (testing "Deleting works"
    (let [transition-present-get-url (str "/transition/"
                                          (id-as-string
                                           transition-to-delete-datomic
                                           :transition/id))
          actual-resp (p.test/response-for service :delete transition-present-get-url)
          conn (datomic.core/connect!)
          id (:id (transition-to-delete-view))
          transition-in-db (dh.entity/find-by-id conn :transition/id id)]
      (is (= 204 (:status actual-resp)))
      (is (not transition-in-db))))

  (testing "non uuid gives 204"
    (let [transition-present-get-url "/transition/39384"
          actual-resp (p.test/response-for service :delete transition-present-get-url)]
      (is (= 204 (:status actual-resp)))))

  (testing "not present uuid gives 204"
    (let [transition-present-get-url (str "/transition/" (p-helper/uuid))
          actual-resp (p.test/response-for service :delete transition-present-get-url)]
      (is (= 204 (:status actual-resp))))))
