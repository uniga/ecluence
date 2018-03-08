# Ecluence
Confluence integration plugin for Eclipse that provides access to context-dependent Confluence content within Eclipse.

Ecluence shows Confluence pages in a view in the workbench. Confluence pages are cached locally and read into memory for fast response times (at the cost of an initial load time). The plugin regularly fetches updated content. If no connection to the Confluence site is available, the plugin serves cached content.

## Update site
An update site for the plugin is available at <http://uniga.dk/ecluence/updates>.

## Configuration
Configuration is required for using the Ecluence. Typical steps are as follows:

1. Add the *Confluence View* to the Eclipse workbench (via menu: *Window > Show View > Other...*).
2. Add a Confluence location in the *Ecluence* Preference Page. Enter the base URL including the protocol (e.g., 'https://my-project.atlassian.net').
3. Provide credentials for accessing the Confluence location, when required. A dialog window will appear when Ecluence first attempts to retrieve content.
4. Add labels to Confluence pages that should be available in the Confluence View. The simplest is to add the **eclipse-index** label in order to index a page that should always be available in the view. (See details below.)
5. Install a template for formatting of pages in the Confluence View in the *Ecluence > Templates* Preference Page.

## Configuration of content matchers
Pages are shown in the Confluence View based on rules implemented by content matchers. The simplest is to use one of the existing matchers in the Ecluence plugin (but see below on extending with new content matchers). Currently, two types of matching are supported: index page matching and label-to-code matching.

### Index page matching
The simplest contribution to make is an index page. Index pages are shown regardless of the current selection.
1. Find a Confluence page that you want to make available from within Eclipse.
2. Add a label **ecluence-index** to the page; this makes the plugin cache the page.
3. Click the refresh icon to fetch the page from Confluence and it should appear in the list.

### Label-to-code matching
Ecluence reacts to selections you make in the workspace (in the package explorer, editors, etc.) by matching pages with the **code-context** label. 
1. Find a Confluence page that you want to make available from within Eclipse.
2. Add a label **code-context** to the page; this makes Ecluence cache the page.
3. Add a label using one of the patterns in the table below to the page (replace example 'foo' and 'bar' terms with meaningful names).

| Page with label | matches if <this> is selected in Eclipse |
| --- | --- |
| **code-class-prefix-foo**   | a Java class that has a name that begins with 'foo' (case insensitive) |
| **code-class-suffix-bar**   | a Java class that has a name that ends with 'bar' (case insensitive) |
| **code-project-prefix-foo** | a Java project (or any file within it) that has a name that begins with 'foo' (case insensitive) |
| **code-project-suffix-bar** | a Java project (or any file within it) that has a name that ends with 'bar' (case insensitive) |
| **code-extends-foobar**     | a Java class extends a class with the name 'foobar' (case insensitive).|
| **code-ant-prefix-foo**     | if an Ant project (or any file within it) that has a name that begins with 'foo' (case insensitive) |
| **code-ant-suffix-bar**     | if an Ant project (or any file within it) that has a name that ends with 'bar' (case insensitive) |

### Extending with new content matchers
Ecluence can be extended with content contributions via the plugin extension point *dk.uniga.ecluence.core.contentContribution*. 

As an example, see the *dk.uniga.ecluence.jdt* plugin, which is included verbatim below. This plugin defines an extension containing: 
1. A content cache, *labelledContentCacheProvider*, which collects and caches all Confluence pages with the label 'code-context'. 
2. A *contentMatcherProvider* implemented by the class AntContentMatcherProvider. This matches pages in the above content cache to the name of a selected Ant project in the Eclipse workbench by the matching labels 'code-ant-prefix-<name>' or 'code-ant-suffix-<name>' (see above).
3. A *contentMatcherProvider* implemented by the class JavaContentMatcherProvider. This matches pages in the content cache to the name of a selected  

The extension also includes an *editorSelectionAdapter* implemented by the class JavaEditorSelectionAdapter, which adapts the current cursor position in a Java editor to a selection of a IJavaElement (defined in Eclipse JDT) that can be matched to pages using the above rules.

	<plugin>
	   <extension
	         point="dk.uniga.ecluence.core.contentContribution">
	      <contentContribution>
	         <labelledContentCacheProvider
	               id="contentCodeContext"
	               type="context"
	               labels="code-context">
	         </labelledContentCacheProvider>
	         <contentMatcherProvider
	               className="dk.uniga.ecluence.jdt.AntContentMatcherProvider"
	               contentCacheProviderId="contentCodeContext">
	         </contentMatcherProvider>
	         <contentMatcherProvider
	               className="dk.uniga.ecluence.jdt.JavaContentMatcherProvider"
	               contentCacheProviderId="contentCodeContext">
	         </contentMatcherProvider>
	      </contentContribution>
	   </extension>
	   <extension
	         point="dk.uniga.ecluence.ui.editorSelectionAdapter">
	      <editorSelectionAdapter
	            className="dk.uniga.ecluence.jdt.JavaEditorSelectionAdapter">
	      </editorSelectionAdapter>
	   </extension>
	</plugin>


## License
Copyright (c) 2017, 2018 Uniga.

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License
v1.0 which accompanies this distribution, and is available at
<http://www.eclipse.org/legal/epl-v10.html>

Contributors:
    Mikkel R. Jakobsen - initial API and implementation
 