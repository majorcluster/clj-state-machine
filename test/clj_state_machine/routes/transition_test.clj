(ns clj-state-machine.routes.transition-test
  (:require [clojure.test :refer :all]
            [integration_core :refer :all]
            [io.pedestal.test :as p.test]
            [clj-state-machine.db.workflow :as db.workflow]
            [clj-state-machine.db.transition :as db.transition]
            [clj-state-machine.db.config :as db.config]
            [clojure.set :as cset]
            [pedestal-api-helper.params-helper :as p-helper]
            [datomic-helper.entity :as dh.entity])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(setup)

(def workflow-present-datomic
  {:workflow/id (p-helper/uuid)
   :workflow/name "checkout"})

(defn workflow-present-url-piece []
  (str "/workflow/" (:workflow/id workflow-present-datomic)))

(def transition-present-datomic
  {:transition/id (p-helper/uuid)
   :transition/name "checkout"})

(def transition-to-update-datomic
  {:transition/id (p-helper/uuid)
   :transition/name "express-checkout"})

(def transition-mobile-checkout-present-datomic
  {:transition/id (p-helper/uuid)
   :transition/name "mobile-checkout"})

(def transition-to-delete-datomic
  {:transition/id (p-helper/uuid)
   :transition/name "delete-me"})

(defn- transition-present-view
  []
  {:id (get transition-present-datomic :transition/id)
   :name (get transition-present-datomic :transition/name)})

(defn- all-transitions-view
  []
  [{:id (-> transition-present-datomic
            :transition/id
            (p-helper/uuid-as-string))
    :name (get transition-present-datomic :transition/name)}
   {:id (-> transition-mobile-checkout-present-datomic
            :transition/id
            (p-helper/uuid-as-string))
    :name (get transition-mobile-checkout-present-datomic :transition/name)}])

(defn- transition-to-update-view
  []
  {:id (get transition-to-update-datomic :transition/id)
   :name (get transition-to-update-datomic :transition/name)})

(defn- transition-to-delete-view
  []
  {:id (get transition-to-delete-datomic :transition/id)
   :name (get transition-to-delete-datomic :transition/name)})

(defn- insert-test-data []
  (let [workflow-id (:workflow/id workflow-present-datomic)]
    (db.workflow/upsert! workflow-present-datomic)
    (db.transition/upsert! workflow-id transition-present-datomic)
    (db.transition/upsert! workflow-id transition-mobile-checkout-present-datomic)))

(insert-test-data)

(deftest get-transition
  (testing "Already present transition is gotten"
    (let [expected-resp (assoc base-message :payload (transition-present-view))
          transition-present-get-url (str "/transition/"
                                        (id-as-string
                                          transition-present-datomic
                                          :transition/id))
          actual-resp (p.test/response-for (:core @test-server) :get transition-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 200 (:status actual-resp)))))

  (testing "non uuid gives not found"
    (let [expected-resp {:message "transition was not found with given id"}
          transition-present-get-url "/transition/39384"
          actual-resp (p.test/response-for (:core @test-server) :get transition-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp)))))

  (testing "not present uuid gives not found"
    (let [expected-resp {:message "transition was not found with given id"}
          transition-present-get-url (str "/transition/" (p-helper/uuid))
          actual-resp (p.test/response-for (:core @test-server) :get transition-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp)))))

  (testing "When no id is sent all transitions are returned"
    (let [actual-resp (p.test/response-for (:core @test-server) :get "/transition")
          body-map (->> actual-resp
                        :body
                        (json-as-map)
                        :payload)]
      (is (cset/subset? (set (all-transitions-view)) (set body-map)))
      (is (= 200 (:status actual-resp))))))

(deftest post-transition
  (testing "insert with mandatory params works"
    (let [new-transition (dissoc (transition-present-view) :id)
          new-name "dispatching"
          new-transition (assoc new-transition :name new-name)
          actual-resp (p.test/response-for (:core @test-server)
                                           :post (str (workflow-present-url-piece) "/transition")
                                           :headers json-header
                                           :body (map-as-json new-transition))
          body-as-map (json-as-map (:body actual-resp))
          id-gotten (get-in body-as-map [:payload :id] 0)
          id-gotten-as-uuid (UUID/fromString id-gotten)
          conn (db.config/connect!)
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
    (let [actual-resp (p.test/response-for (:core @test-server)
                                           :post (str (workflow-present-url-piece) "/transition")
                                           :headers json-header
                                           :body "")
          expected-resp {:message "", :validation-messages [{:field "name"
                                                              :message "Field :name is not present"}]}]
      (is (= 400 (:status actual-resp)))
      (is (= (map-as-json expected-resp) (:body actual-resp)))))
  (testing "insert with missing workflow-id gives 404"
    (let [actual-resp (p.test/response-for (:core @test-server)
                                           :post "/transition"
                                           :headers json-header
                                           :body "")]
      (is (= 404 (:status actual-resp))))))

(deftest patch-transition
  (testing "patch with mandatory params works"
    (let [new-name "preparing"
          new-transition (assoc (transition-to-update-view) :name new-name)
          actual-resp (p.test/response-for (:core @test-server)
                                           :patch (str (workflow-present-url-piece) "/transition")
                                           :headers json-header
                                           :body (map-as-json new-transition))
          id (:id (transition-to-update-view))
          conn (db.config/connect!)
          transition-in-db (dh.entity/find-by-id conn :transition/id id)
          workflow-in-db (dh.entity/find-by-id conn :workflow/id (:workflow/id workflow-present-datomic))
          workflow-transitions (get workflow-in-db :workflow/transitions [])
          found-in-workflow (some #(= (:transition/id %) id) workflow-transitions)]
      (is (= 204 (:status actual-resp)))
      (is (= (:transition/name transition-in-db) new-name))
      (is found-in-workflow)))
  (testing "patch with missing mandatory params gives 400"
    (let [actual-resp (p.test/response-for (:core @test-server)
                                           :patch (str (workflow-present-url-piece) "/transition")
                                           :headers json-header
                                           :body "")
          expected-resp {:message "", :validation-messages [{:field "id" :message "Field :id is not present"}
                                                            {:field "name" :message "Field :name is not present"}]}]
      (is (= 400 (:status actual-resp)))
      (is (= (map-as-json expected-resp) (:body actual-resp)))))
  (testing "patch with missing workflow-id gives 404"
    (let [actual-resp (p.test/response-for (:core @test-server)
                                           :patch "/transition"
                                           :headers json-header
                                           :body "")]
      (is (= 404 (:status actual-resp))))))

(deftest delete-transition
  (testing "Deleting works"
    (let [transition-present-get-url (str "/transition/"
                                      (id-as-string
                                        transition-to-delete-datomic
                                        :transition/id))
          actual-resp (p.test/response-for (:core @test-server) :delete transition-present-get-url)
          conn (db.config/connect!)
          id (:id (transition-to-delete-view))
          transition-in-db (dh.entity/find-by-id conn :transition/id id)]
      (is (= 204 (:status actual-resp)))
      (is (not transition-in-db))))

  (testing "non uuid gives 204"
    (let [transition-present-get-url "/transition/39384"
          actual-resp (p.test/response-for (:core @test-server) :delete transition-present-get-url)]
      (is (= 204 (:status actual-resp)))))

  (testing "not present uuid gives 204"
    (let [transition-present-get-url (str "/transition/" (p-helper/uuid))
          actual-resp (p.test/response-for (:core @test-server) :delete transition-present-get-url)]
      (is (= 204 (:status actual-resp)))))
  )
