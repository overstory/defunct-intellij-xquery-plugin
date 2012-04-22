xquery version '1.0-ml';

declare namespace apidoc="http://marklogic.com/xdmp/apidoc";
declare namespace xhtml="http://www.w3.org/1999/xhtml";

declare function local:remove-namespaces ($node as node())
{
	typeswitch ($node)
	case element(apidoc:example) return <example xml:space="preserve">{if ($node/xhtml:pre) then $node/xhtml:pre/node() else $node/node()}</example>
	case element() return element { $node/local-name(.) } { $node/@*, local:remove-namespaces ($node/node()) }
	default return $node
};

let $version := xdmp:get-request-field('version', "5.0")
let $base-uri := fn:concat ('http://developer.marklogic.com:8040/', $version, 'doc/')
let $get-options :=
	<options xmlns="xdmp:document-get" xmlns:http="xdmp:http">
		<format>xml</format>
	</options>

let $fns := xdmp:document-get (fn:concat($base-uri,'functionSummary.xqy'), $get-options)

let $files := distinct-values (
	for $href in $fns//*:a/@href
	where fn:starts-with($href,fn:concat('#display.xqy?fname=http://pubs/', $version, 'doc/apidoc/'))
	return fn:substring-before(fn:substring-after($href, fn:concat('#display.xqy?fname=http://pubs/', $version, 'doc/apidoc/')),'&amp;')
)

let $functions :=
	<functions base="{$base-uri}">{
		for $file in $files
		let $module := xdmp:document-get (fn:concat($base-uri, '/apidoc/', $file), $get-options)/element()
		return local:remove-namespaces ($module//apidoc:function)
	}</functions>


return
xdmp:document-insert ("/apidocs/ml-functions.xml", $functions)

, "OK"


