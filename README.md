## What is GeneViewer?


GeneViewer is a Google Web Toolkit (GWT) widget containing a [processingjs][0] sketch for visualizing gene models.

![GeneViewer](https://raw.githubusercontent.com/timeu/GeneViewer/master/geneviewer.png "GeneViewer")


## How do I use it?

Following steps are required:  

```JAVA
GeneViewer geneviewer = new GeneViewer();
geneviewer.load(new Runnable() {
   @Override
   public void run() {
       GWT.log("GeneViewer loaded");
       // Interact with sketch
       JsArrayMixed data = getData();
       geneviewer.setGeneData(data);
   }
});
```
To display the gene models the user has to call the `setGEneData(JsArrayMixed data)` function. 
The `JsArrayMixed` data consists of a list of genes in JSON format. There are 2 visualization modes: 
 1. Simple gene models without any features (See [sample data][1]).
 2. Gene Models with features (see [sample data][2] and screenshot).

**Example of data with features:**

```JSON
   [
     [10421840,10424113,1,"AT2G24530.1",
       [
         [10422416,10422596,1,"five_prime_UTR"],
         [10422272,10422312,1,"five_prime_UTR"],
         [10421840,10421930,1,"five_prime_UTR"],
         [10422416,10424113,1,"exon"],
         [10423820,10424113,1,"three_prime_UTR"],
         [10422272,10422312,1,"exon"],
         [10421840,10421930,1,"exon"],
         [10422596,10423820,1,"CDS"]
       ]
     ],
     [....]
   ]
```

The data can be loaded this way: 
 
```JAVA
final String jsonData = GET_FROM_CLIENTBUNDLE OR AJAX CALL
JsArrayMixed data = JsonUtils.safeEval(jsonData);
```

## How do I install it?

If you're using Maven, you can add the following to your `<dependencies>`
section:

```xml
    <dependency>
      <groupId>com.github.timeu.gwtlibs.geneviewer</groupId>
      <artifactId>geneviewer</artifactId>
      <version>1.0.0</version>
    </dependency>
```

GeneViewer uses [GWT 2.7's][3] new [JSInterop feature][4] and thus it has to be enabled in the GWT compiler args.
For maven:
```xml
<compilerArgs>
    <compilerArg>-XjsInteropMode</compilerArg>
    <compilerArg>JS</compilerArg>
</compilerArgs>
```
or passing it to the compiler via `-XjsInteropMode`

You can also download the [jar][5] directly or check out the source using git
from <https://github.com/timeu/geneviewer.git> and build it yourself. Once
you've installed LDViewer, be sure to inherit the module in your .gwt.xml
file like this:

```xml
    <inherits name='com.github.timeu.gwtlibs.geneviewer.GeneViewer'/>
```

## Where can I learn more?

 * Check out the [sample app][6] ([Source Code][7]) for a full example of using GeneViewer.
 
[0]: http://processingjs.org
[1]: https://github.com/timeu/GeneViewer/blob/master/geneviewer-sample/src/main/resources/sample/client/data/genes.json
[2]: https://github.com/timeu/GeneViewer/blob/master/geneviewer-sample/src/main/resources/sample/client/data/genes_with_features.json
[3]: http://www.gwtproject.org/release-notes.html#Release_Notes_2_7_0_RC1
[4]: https://docs.google.com/document/d/1tir74SB-ZWrs-gQ8w-lOEV3oMY6u6lF2MmNivDEihZ4/edit#
[5]: https://github.com/timeu/GeneViewer/releases
[6]: http://timeu.github.io/GeneViewer
[7]: https://github.com/timeu/GeneViewer/tree/master/geneviewer-sample 
