package plugins.Spider.web;

import java.io.IOException;
import java.net.URI;

import plugins.Spider.Spider;

import freenet.client.HighLevelSimpleClient;
import freenet.clients.http.PageNode;
import freenet.clients.http.RedirectException;
import freenet.clients.http.Toadlet;
import freenet.clients.http.ToadletContext;
import freenet.clients.http.ToadletContextClosedException;
import freenet.node.NodeClientCore;
import freenet.support.HTMLNode;
import freenet.support.MultiValueTable;
import freenet.support.api.HTTPRequest;

public class ConfigPageToadlet extends Toadlet {

	final Spider spider;
	private final NodeClientCore core;
	
	protected ConfigPageToadlet(HighLevelSimpleClient client, Spider spider, NodeClientCore core) {
		super(client);
		this.spider = spider;
		this.core = core;
	}

	@Override
	public String path() {
		return "/spider/config";
	}

	public void handleMethodGET(URI uri, final HTTPRequest request, final ToadletContext ctx) 
	throws ToadletContextClosedException, IOException, RedirectException {
		ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(Spider.class.getClassLoader());
		try {
			ConfigPage page = new ConfigPage(spider);
			PageNode p = ctx.getPageMaker().getPageNode(Spider.pluginName, ctx);
			HTMLNode pageNode = p.outer;
			HTMLNode contentNode = p.content;
			page.writeContent(request, contentNode);
			writeHTMLReply(ctx, 200, "OK", null, pageNode.generate());
		} finally {
			Thread.currentThread().setContextClassLoader(origClassLoader);
		}
	}

	public void handleMethodPOST(URI uri, HTTPRequest request, final ToadletContext ctx) throws ToadletContextClosedException, IOException, RedirectException {
		ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(Spider.class.getClassLoader());

		String formPassword = request.getPartAsString("formPassword", 32);
		if((formPassword == null) || !formPassword.equals(core.formPassword)) {
			MultiValueTable<String,String> headers = new MultiValueTable<String,String>();
			headers.put("Location", "/spider/config");
			ctx.sendReplyHeaders(302, "Found", headers, null, 0);
			return;
		}

		try {
			PageNode p = ctx.getPageMaker().getPageNode(Spider.pluginName, ctx);
			HTMLNode pageNode = p.outer;
			HTMLNode contentNode = p.content;
	
			WebPage page = new ConfigPage(spider);
	
			page.processPostRequest(request, contentNode);
			page.writeContent(request, contentNode);
	
			writeHTMLReply(ctx, 200, "OK", null, pageNode.generate());
		} finally {
			Thread.currentThread().setContextClassLoader(origClassLoader);
		}
	}
}
