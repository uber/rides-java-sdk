${title} - ${date}
${snippet}

| Download      | Description |
| ------------- |-------------|
[Visit GitHub!](https://www.github.com).
<% assets.each{ asset -> %>| <%= "[asset.title](asset.download)" %> | <%= asset.description %> |\n<%}%>