#  Feedback Box Component

## What is it?
A .js file you can embed on any page to add a feedback form. It is built using Rum and Shadow-cljs.

<img width="272" alt="Screen Shot 2022-01-09 at 11 12 46 PM" src="https://user-images.githubusercontent.com/451489/148724673-1a7e0134-a7d8-40eb-abfa-4294306e74c6.png">

## Build Instructions

For a release build:
```console
shadow-cljs release feedbackbocks 

```

For an interactive watch build
```console
shadow-cljs watch feedbackbocks
```

## Usage

This component expects a `<div>` with an `id` of `dartar`.

```html
<div id='dartar'
     data-endpoint="/feedback" 
     data-greeting="Send us feedback about this page"
     data-feedback-placeholder="Wowzers you guys..." 
     data-email-placeholder="user@jpc.com"></div>
```

As well as the aft (csrf anti forgery token)

```html
<div id='aft' style='display:none;' value={{aft}} data-aft={{aft}}></div>
<!--the antiforgery token must be provided by the server rendering this file;; selmer is used here-->
```

As well as the id called fbbi

```html
<div id='fbbi'></div>
```


  Finally, in index.html
  calls and intializes

  ```html
  <script src="/js/feedbackbocks.js" type="text/javascript"></script>
    <script>
        feedback_box.core.init();
    </script>
  ```





The core.cljs file grabs the aft from the html page 
and also gets the textarea.value from the feedback bocks.


  ```clj
*aft* (.getAttribute (. js/document (getElementById "aft")) "data-aft")
in (.-value (. js/document (getElementById "feedback-input")))
  ```



The POST is done via cljs-ajax

```clj
(POST endpoint
  { :params {:feedback-content in}
    :headers {"x-xsrf-token" *aft*}
    :format :text})
  (.log js/console "aft: " *aft*)
  (.log js/console "input: " in)
```


More :params can be added to the :params map.




On the server `.clj` file you will need a route to accept the POST

```clj
  (POST "/feedback"  [_ :as r]  
    (let [client-url   (get (:headers r) "referer")
          client-email (:special-auth-email (:session r))
          bod (slurp (:body r))
          bbod (clojure.edn/read-string bod)
          feedback (:feedback bbod)]  
      (prn "; r " r )
      (prn "; email " client-email)
      (prn "client-url;; " client-url)
      (prn "bbod;; " bbod)
      (prn "feedback;; " feedback)
      ;;save the feedback to the server
      (prn "save the feedback to the server here.")
      {:status 200  
       :headers {"Content-Type" "text"} 
       :body (str "received from " client-email " at " client-url)}))
```

That is an example using Ring and Compojure.  In general, one can break the request into relevant subkeys, but one must `clojure.edn/read-string` on the body of the :format :text POST.
