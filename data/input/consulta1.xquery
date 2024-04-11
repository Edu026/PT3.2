declare option output:method "xml";
declare option output:indent "yes";

<posts>{
  for $p in (
      for $p in /posts/row
      order by xs:integer($p/@ViewCount) descending
      return $p
  )[position() le 10000]
  return 
      <post>{$p}</post>
}</posts>