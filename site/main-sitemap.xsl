<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:sitemap="http://www.sitemaps.org/schemas/sitemap/0.9"
	xmlns:image="http://www.google.com/schemas/sitemap-image/1.1"
	exclude-result-prefixes="sitemap image">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>

	<xsl:template match="/">
		<html>
			<head>
				<meta name="viewport" content="width=device-width, initial-scale=1"/>
				<title>XML Sitemap</title>
				<style>
					body{margin:0;background:#f7f8fa;color:#24292f;font:14px/1.5 -apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif}
					main{max-width:1100px;margin:48px auto;padding:0 24px}
					h1{margin:0 0 8px;font-size:28px;font-weight:600}
					p{margin:0 0 24px;color:#57606a}
					.table-wrap{overflow-x:auto;border:1px solid #d0d7de;border-radius:8px;background:#fff}
					table{width:100%;border-collapse:collapse}
					th,td{padding:12px 16px;border-bottom:1px solid #d8dee4;text-align:left;vertical-align:top}
					th{background:#f6f8fa;font-weight:600;white-space:nowrap}
					tr:last-child td{border-bottom:0}
					a{color:#0969da;text-decoration:none;overflow-wrap:anywhere}
					a:hover{text-decoration:underline}
					.number{text-align:right;white-space:nowrap}
				</style>
			</head>
			<body>
				<main>
					<h1>XML Sitemap</h1>
					<xsl:choose>
						<xsl:when test="sitemap:sitemapindex">
							<p>This sitemap index contains <xsl:value-of select="count(sitemap:sitemapindex/sitemap:sitemap)"/> sitemaps.</p>
							<div class="table-wrap">
								<table>
									<thead><tr><th>URL</th><th>Last modified</th></tr></thead>
									<tbody>
										<xsl:for-each select="sitemap:sitemapindex/sitemap:sitemap">
											<tr>
												<td><a href="{sitemap:loc}"><xsl:value-of select="sitemap:loc"/></a></td>
												<td><xsl:value-of select="sitemap:lastmod"/></td>
											</tr>
										</xsl:for-each>
									</tbody>
								</table>
							</div>
						</xsl:when>
						<xsl:otherwise>
							<p>This sitemap contains <xsl:value-of select="count(sitemap:urlset/sitemap:url)"/> URLs.</p>
							<div class="table-wrap">
								<table>
									<thead><tr><th>URL</th><th class="number">Images</th><th>Last modified</th></tr></thead>
									<tbody>
										<xsl:for-each select="sitemap:urlset/sitemap:url">
											<tr>
												<td><a href="{sitemap:loc}"><xsl:value-of select="sitemap:loc"/></a></td>
												<td class="number"><xsl:value-of select="count(image:image)"/></td>
												<td><xsl:value-of select="sitemap:lastmod"/></td>
											</tr>
										</xsl:for-each>
									</tbody>
								</table>
							</div>
						</xsl:otherwise>
					</xsl:choose>
				</main>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>