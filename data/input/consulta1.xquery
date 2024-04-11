declare option output:method "xml";
declare option output:indent "yes";

<posts>{
  for $p in /posts/row
  order by xs:integer($p/@ViewCount) descending
  return
    if ($p/@Title != " ") then
        <post>
          <title>{$p/@Title/string()}</title>
          <viewCount>{$p/@ViewCount/string()}</viewCount>
        </post> 
}</posts>
