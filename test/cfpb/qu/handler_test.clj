(ns cfpb.qu.handler-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :refer :all]
            [cfpb.qu.handler :refer [app]]))

(fact "the index URL redirects to /data"
      (app (request :get "/"))
      => (contains {:status 302
                    :headers {"Location" "/data"}}))

(facts "about /data"
       (prerequisite (#'cfpb.qu.data/get-datasets) => [])

       (fact "it returns successfully"
             (app (request :get "/data"))
             => (contains {:status 200
                           :headers {"Content-Type" "text/html;charset=UTF-8"}})

             (app (request :get "/data.xml"))
             => (contains {:status 200
                           :headers {"Content-Type" "application/xml;charset=UTF-8"}})))

(facts "about /data/dataset"
       (fact "it returns successfully when the dataset exists"
             (prerequisite (#'cfpb.qu.data/get-metadata "good-dataset") => {})

             (app (request :get "/data/good-dataset"))
             => (contains {:status 200
                           :headers {"Content-Type" "text/html;charset=UTF-8"}})

             (app (request :get "/data/good-dataset.xml"))
             => (contains {:status 200
                           :headers {"Content-Type" "application/xml;charset=UTF-8"}}))

       (fact "it returns a 404 when the dataset does not exist"
             (prerequisite (#'cfpb.qu.data/get-metadata "bad-dataset") => nil)

             (app (request :get "/data/bad-dataset"))
             => (contains {:status 404})))
