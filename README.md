#  Feedback Box Component

## What is it?
A .js file you can embed on any page to add a feedback form. It is built using Rum and Shadow-cljs.

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
     data-email-placeholder="user@jpc.com">
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
