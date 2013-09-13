package uber.kaba.markup.parser;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 *
 *Для форматирования сообщений используется собственный язык разметки WakabaMark. Общий смысл команд форматирования близок к вики-разметке.

 * Несколько пустых строк подряд игнорируются.
 * Выделение текста происходит с помощью звёздочек или символов подчёркивания. Текст в *одинарных звёздочках* (_или символах подчёркивания_) станет курсивным, а **в двойных** (__можно и вот так__) — полужирным. Алсо, комбинируя эти методы форматирования, можно написать текст полужирным курсивом.
 * Для того, чтобы зачеркнуть предыдущий символ, после него нужно поставить «^H». К примеру, «тест^H^H^H^H» превратится в «тест». В настоящий момент эта возможность выпилена на многих бордах в связи с ошибками в движке скрипта, позволяющими с помощью этого самого ^H зачёркивать весь последующий текст на странице. Стоит отметить, что на уютненьком Луркоморье есть специальные статьи про ^H и ^W.
 * Символы «*», «+» или «-» с последующим пробелом в начале каждой строки для создания маркированных списков. Разрывы строк в пределах списков недопустимы, а если и будут, то «порвут» блок. Если написать несколько управляющих символов подряд (* * * тест), стандартный парсер выдаст довольно-таки нестандартный результат.
 * Текст «1. » для начала нумерованного списка. Последующие пункты списка могут быть любыми, скрипт автоматически пронумерует их по порядку (например, можно составить список, состоящий из одних единичек).
 * Обратные апострофы служат `для оформления кода`. Код, уже содержащий обратные апострофы, следует заключать в ``несколько «`» подряд``.
 * Для оформления больших блоков кода, а также кода с отступами, на каждой строке нужно поставить 4 пробела ( ) или один символ табуляции (который в ХТМЛ &#09;).
 * Символ «>» в начале строки для >цитирования. Несколько таких строк подряд объединяются в один блок. Примечательно, что цитирование при помощи двойного >> — верный способ выдать себя за ньюфага.
 * Текст, содержащий ссылку с протоколами http:, https:, ftp:, mailto:, news:, или irc: автоматически преобразуется в гиперссылку.
 * >>номер становится ссылкой на сообщение под данным номером (если, конечно, сообщение с этим номером существует в данный момент на доске). Вероятность возникновения ссылки, если в посте написать его же номер, досконально не изучена, но обычно ссылка не возникает.

Дополнительные возможности

 *  можно превратить любой текст в спойлер, используя %%двойные символы процента%% по каждую сторону спойлера. Ну ты понел.
 * Также можно сделать псевдоспойлер, скопировав и вставив несколько раз символ █, получая в итоге полоску вида ███████████.

 *
 *
 * @author alex
 */


public class KabaMarkupParser {

    private static final String CODE_START = "code:",
                                CODE_END = ":code",
                                NO_PARSE = ":noparse:",
                                MATH_TYPE = "math:",LINE_BR="<br/>";
    
    private static final Pattern YOUTUBE_PATTERN = Pattern.compile("http://(w{3}.)?youtube.com([a-zA-Z0-9\\?\\&/]*)v([=/])(?<modelId>[a-zA-Z0-9-_]*)", Pattern.CASE_INSENSITIVE),
                                 APP_LINK_PATTEN = Pattern.compile("\\&gt;\\&gt;([0-9]+)*", Pattern.CASE_INSENSITIVE),
                                 PASTE_LINK_PATTERN = Pattern.compile(AppMode.PASTE.name()+"\\-(?<modelId>[0-9]+)*",Pattern.CASE_INSENSITIVE),
                                 SHARE_LINK_PATTERN = Pattern.compile(AppMode.SHARE.name()+"\\-(?<modelId>[0-9]+)*",Pattern.CASE_INSENSITIVE);
    

    /**
     * протоколы используемые в ссылках (mailto идет отдельно тк пишется без //)
     */
    private enum Protocols {
        http, https, news, irc, ftp
    }
    
    
    private StringBuilder data = new StringBuilder();
    
    private String pasteUrl,shareUrl;

    private boolean enablePaste,enableShare,enableYoutube,enableMath;

    private AppMode mode;
    
    private static final VelocityEngine ve = new VelocityEngine();

    private static final Template pasterTemplate,youtubeTemplate,shareTemplate;
     
    static {
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        ve.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, "org.apache.velocity.slf4j.Slf4jLogChute" );
        
        try {
        
         /*  first, get and initialize an engine  */
        ve.init();
        /*  next, get the Template  */
        pasterTemplate = ve.getTemplate( "pasterTemplate.vsl" );
        youtubeTemplate = ve.getTemplate( "youtubeTemplate.vsl" );
        shareTemplate = ve.getTemplate( "shareTemplate.vsl" );
        
        
        
    } catch (Exception e) {
        throw new IllegalStateException(e);
    }
        
    }
    
    
    
    public StringBuilder getData() {
        return data;
    }

    public String getPasteUrl() {
        return pasteUrl;
    }

    public KabaMarkupParser setPasteUrl(String pasteUrl) {
        this.pasteUrl = pasteUrl;
        return this;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public KabaMarkupParser setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
        return this;
    }

    public AppMode getMode() {
        return mode;
    }

    public KabaMarkupParser setMode(AppMode mode) {
        this.mode = mode;
        return this;
    }

   
    public boolean isEnablePaste() {
        return enablePaste;
    }

    
    public KabaMarkupParser enablePaste(boolean value) {
        this.enablePaste=value;
        return this;
    }
    
    public KabaMarkupParser enableShare(boolean value) {
        this.enableShare=value;
        return this;
    }

    public boolean isEnableShare() {
        return enableShare;
    }


    public boolean isEnableYoutube() {
        return enableYoutube;
    }

    public KabaMarkupParser enableYoutube(boolean value) {
        this.enableYoutube=value;
        return this;
    }

    public boolean isEnableMath() {
        return enableMath;
    }

    
   public KabaMarkupParser enableMath(boolean value) {
        this.enableMath=value;
        return this;
    }

    public KabaMarkupParser setSource(String source) {

        clear();

        data.append(escapeHtml(source));

        return this;
    }
    
    
    
    public KabaMarkupParser parseUsername() throws IOException {

        String name = data.toString();
        
         name = replaceSpaces(name);
       

        // title = parseLine(title);

        data.delete(0, data.length());

        data.append(name);

        return this;
    }

    public KabaMarkupParser parseTitle() throws IOException {

        String title = data.toString();
        
        title = replaceSpaces(title);
        
        title = prettyText(title);

        // title = parseLine(title);

        data.delete(0, data.length());

        data.append(title);

        return this;
    }

    /**
     * PARSE IN THE NAME OF THE EMPEROR!
     * @return
     * @throws IOException
     */
    public KabaMarkupParser parseAll() throws IOException {

       // System.out.println("__start parse");

      
        try (LineNumberReader r = new LineNumberReader(new StringReader(data.toString()))) {
      
            clear();

            String line;

            StringBuilder block = new StringBuilder();
            boolean codeStarts = false, noParse = false, wasMath = false;

            while ((line = r.readLine()) != null) {

                if (!codeStarts && StringUtils.isBlank(line)) {

                    data.append(LINE_BR);
                    continue;

                }
                /*
                if (line.length()>130) {
                String bl;
                for (int i=0;i<line.length();i+=130) {
                bl = line.substring(i);
                }
                }
                 */

                if (noParse) {
                    data.append(line).append(LINE_BR);
                    continue;
                }

                if (line.contains(NO_PARSE)) {

                    String pre = line.substring(0, line.indexOf(NO_PARSE));

                    if (!StringUtils.isBlank(pre)) {
                        pre = parseLine(pre);
                    }

                    data.append(pre).append(LINE_BR);


                    line = line.substring(line.indexOf(NO_PARSE) + NO_PARSE.length());

                    data.append(line).append(LINE_BR);

                    block.delete(0, block.length());

                    noParse = true;

                    continue;
                }

                if (codeStarts) {
                    block.append(line).append("\n");
                }

                if (line.contains(CODE_START)) {

                    String pre = line.substring(0, line.indexOf(CODE_START));
                    pre = parseLine(pre);

                    data.append(pre).append(LINE_BR);

                    line = line.substring(line.indexOf(CODE_START) + CODE_START.length());
                    
                    if (line.startsWith(MATH_TYPE)) {
                        line = line.substring(MATH_TYPE.length());
                        wasMath = true;
                    }

                    block.append(line);
                    codeStarts = true;
                }


                if (line.contains(CODE_END)) {

                    int pos = block.indexOf(CODE_END);
                    if (pos != -1) {
                        //ACHTUNG! CODE_END already in buffer!

                        block.delete(pos, block.length());

                    } else {

                        block.append(line.substring(0, line.indexOf(CODE_END)));

                    }

                    line = line.substring(line.indexOf(CODE_END) + CODE_END.length());

                    codeStarts = false;

                    if (wasMath) {
                        appendMath(block.toString());
                        wasMath = false;

                    } else {
                        appendCode(block.toString());

                    }

                    block.delete(0, block.length());

                    // continue;

                }

                if (codeStarts || StringUtils.isBlank(line)) {
                    continue;
                }


                line = parseLine(line);

                data.append(line).append(LINE_BR);
            }
            
            
            //for lazy fucks who don't use :code
            if (block.length() > 0) {
                if (wasMath) {
                    appendMath(block.toString());                    
                } else {
                    appendCode(block.toString());
                }

            }        
            
        }

        return this;
    }

    private String parseVelocity(VelocityContext context,Template template) throws IOException {
        
        /* now render the template into a StringWriter */
        
        try (StringWriter writer = new StringWriter()) {
            template.merge( context, writer );
        return writer.toString();
        }
    }
    
    
     private String parseTemplate(String line,Pattern p,VelocityContext context,Template template) throws IOException {
    
         Matcher matcher = p.matcher(line);
        StringBuffer sbm = new StringBuffer();
        while (matcher.find()) { 
            context.put("modelId",  matcher.group(matcher.groupCount()) );
            matcher.appendReplacement(sbm, parseVelocity(context,template));
        }

        matcher.appendTail(sbm);
    
        return sbm.toString();
        }
    
    private String parseShare(String line) throws IOException {
        
        VelocityContext context = new VelocityContext();
        context.put("shareUrl", shareUrl);

        return parseTemplate(line, SHARE_LINK_PATTERN, context, shareTemplate);

    }
    
   
    
    private String parsePaste(String line) throws IOException {

        VelocityContext context = new VelocityContext();
        context.put("pasteUrl", pasteUrl);

        return parseTemplate(line, PASTE_LINK_PATTERN, context, pasterTemplate);

    }
    
    private String parseYoutube(String line) throws IOException {
        return parseTemplate(line, YOUTUBE_PATTERN,  new VelocityContext(), youtubeTemplate);
     }

    private void appendCode(String block) {

        data.append("<br/><pre class=\"prettyprint\">")
                .append(block)
                .append("</pre>")
                .append("<br/>");
    }

    private void appendMath(String block) {

        data.append("<br/><p>--math-start--")
                .append(block)
                .append("--math-end--</p>")
                .append("<br/>");
    }

    private String prettyText(String line) {


         //smart curly double quotes
        return line.replaceAll("\"([^<]*)\"", "&#8220;$1&#8221;")

           //smart curly single quotes
        .replaceAll("\'([^<]*)\'", "&#8216;$1&#8217;")

        //dashes
        //em
        .replaceAll("\\-{3}", "&mdash;")

        //en
        .replaceAll("\\-{2}", "&ndash;")
        //...
        .replaceAll("\\.{3}", "&hellip;")
        //(c)
        .replaceAll("\\(c\\)", "&#169;")
        //trade mark
        .replaceAll("\\s+tm.", "&#8482;");
    }

    private String parseLine(String line) throws IOException {

        // line = " " + line; //this needs now for correct parsing

        boolean canPrettify =true;

         /**
         * remove extra spaces
         */
        line = line.replaceAll("\\s+{2}", " ")

        //line = prettyText(line);


        /**
         * ПОРЯДОК РАЗБОРА ВАЖЕН 
         *
         */
        /**
         *  полужирный __тест__ или **текст**
         */
        .replaceAll("\\*{2}([^<]*)\\*{2}", "<strong>$1</strong>")
        .replaceAll("_{2}([^<]*)_{2}", "<strong>$1</strong>")


        /**
         *  курсив _italic_ *italic*
         */
        .replaceAll("\\*([^<]*)\\*", "<em>$1</em>")
        .replaceAll("_([^<]*)_", "<em>$1</em>")


        /**
         *  перечеркнутый текст text^H или ^text^
         */
        .replaceAll("\\s+([^<]*)\\^H", "<strike>$1</strike>")
        .replaceAll("\\^([^<]*)\\^", "<strike>$1</strike>");


        if (line.contains("mailto:")) {
            canPrettify=false;
        }
        /**
         * mailto:
         */
        line = line.replaceAll("mailto:([^<]*)", "<a href='mailto:$1'>$1</a>");

        if (line.toLowerCase().contains("youtube.com")) {
            line = parseYoutube(line);
            canPrettify=false;
             
        } else {
            /**
             *  остальные протоколы
             */
            for (Protocols p : Protocols.values()) {

                if (line.contains(p.name()+"://"))
                    canPrettify=false;

                line = line.replaceAll(p.name() + "://([^<]*)", "<a href='" + p.name() + "://$1'>$1</a>");
            }

        }


        // NO BRACES TILL THIS LINE (no links) !!!!!!
        if (canPrettify) {
            line = prettyText(line);
        }
        
        
        line = line.replaceAll("^[\\s+]?//([^<]*)", "<span class='color2'>$1</span>")
                .replaceAll("^[\\s+]?#([^<]*)", "<span class='color3'>$1</span>");
        
        
        /**
         *  ссылки на посты вида >>21
         */
        
        
                 line = line.replaceAll("\\&gt;\\&gt;([0-9]+)*", 
                         "<a href='" + ( mode == AppMode.PASTE ? pasteUrl : shareUrl) + "/$1'>>>$1</a>&nbsp;");
       
        /*
        switch(mode) {
        
            case PASTE: {
                line = line.replaceAll("\\&gt;\\&gt;([0-9]+)*", "<a href='" + pasteUrl  + "/$1'>>>$1</a>&nbsp;");
                break;
            }
            case SHARE: {
                 line = line.replaceAll("\\&gt;\\&gt;([0-9]+)*", "<a href='" + shareUrl  + "/$1'>>>$1</a>&nbsp;");
                 break;
            }   
        }*/
        
         
         line = parsePaste(line);
         
         // line = line.replaceAll(AppMode.PASTE.name()+"\\-([0-9]+)*", "<a href='" + pasteUrl  + "/$1'>"+AppMode.PASTE.name()+"-$1</a>&nbsp;");
        
       //  line = line.replaceAll(AppMode.SHARE.name()+"\\-([0-9]+)*", "<a href='" + shareUrl  + "/$1'>"+AppMode.SHARE.name()+"-$1</a>&nbsp;");

         line = parseShare(line);

        line = line.replaceAll("^[\\s+]?\\&gt;([^<]*)", "<span class='color1'>>$1</span>");

        /**
         *  спойлер
         */
        line = line.replaceAll("\\%\\%([^<]*)\\%\\%", "<span class='spoiler'>$1</span>");



        return line;
    }

    public String get() {
        return data.toString();
    }

    public void clear() {
        data.delete(0, data.length());
    }
    
    
    // HTML and XML
    //--------------------------------------------------------------------------
    /**
     * <p>Escapes the characters in a <code>String</code> using HTML entities.</p>
     *
     * <p>
     * For example:
     * </p>
     * <p><code>"bread" & "butter"</code></p>
     * becomes:
     * <p>
     * <code>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</code>.
     * </p>
     *
     * <p>Supports all known HTML 4.0 entities, including funky accents.
     * Note that the commonly used apostrophe escape character (&amp;apos;)
     * is not a legal entity and so is not supported). </p>
     *
     * @param str  the <code>String</code> to escape, may be null
     * @return a new escaped <code>String</code>, <code>null</code> if null string input
     *
     * @see #unescapeHtml(String)
     * @see <a href="http://hotwired.lycos.com/webmonkey/reference/special_characters/">ISO Entities</a>
     * @see <a href="http://www.w3.org/TR/REC-html32#latin1">HTML 3.2 Character Entities for ISO Latin-1</a>
     * @see <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">HTML 4.0 Character entity references</a>
     * @see <a href="http://www.w3.org/TR/html401/charset.html#h-5.3">HTML 4.01 Character References</a>
     * @see <a href="http://www.w3.org/TR/html401/charset.html#code-position">HTML 4.01 Code positions</a>
     */
    public static String escapeHtml(String str) {
        
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter((int) (str.length() * 1.5));
            escapeHtml(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            //should be impossible
            throw new UnhandledException(ioe);
        }
    }

    /**
     * <p>Escapes the characters in a <code>String</code> using HTML entities and writes
     * them to a <code>Writer</code>.</p>
     *
     * <p>
     * For example:
     * </p>
     * <code>"bread" & "butter"</code>
     * <p>becomes:</p>
     * <code>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</code>.
     *
     * <p>Supports all known HTML 4.0 entities, including funky accents.
     * Note that the commonly used apostrophe escape character (&amp;apos;)
     * is not a legal entity and so is not supported). </p>
     *
     * @param writer  the writer receiving the escaped string, not null
     * @param string  the <code>String</code> to escape, may be null
     * @throws IllegalArgumentException if the writer is null
     * @throws IOException when <code>Writer</code> passed throws the exception from
     *                                       calls to the {@link Writer#write(int)} methods.
     *
     * @see #escapeHtml(String)
     * @see #unescapeHtml(String)
     * @see <a href="http://hotwired.lycos.com/webmonkey/reference/special_characters/">ISO Entities</a>
     * @see <a href="http://www.w3.org/TR/REC-html32#latin1">HTML 3.2 Character Entities for ISO Latin-1</a>
     * @see <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">HTML 4.0 Character entity references</a>
     * @see <a href="http://www.w3.org/TR/html401/charset.html#h-5.3">HTML 4.01 Character References</a>
     * @see <a href="http://www.w3.org/TR/html401/charset.html#code-position">HTML 4.01 Code positions</a>
     */
    public static void escapeHtml(Writer writer, String string) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("The Writer must not be null.");
        }
        if (string == null) {
            return;
        }
        KabaEntities.HTML40.escape(writer, string);
    }

    private String replaceSpaces(String in) {
        return StringUtils.isEmpty(in) ? in : in.replaceAll("\\n", " ").replaceAll("\\s+{2}", " ");
    }
    
    public static void main(String[] args) throws Exception {


        System.out.println("starting..");
        
        
        
        System.out.println("out={" + getInstance().setSource("fuck http://www.youtube.com/watch?v=l4fN_shVm4s  __http://ya.ru__  http://dirty.ru     \n "
                + " >>2    PASTE-35  &#123; \"ебать колотить\" сука--бля \n"
                + "   >COLORED  и будет свет! --- копирайт (c) и торговая марка tm.  code:xxxx:code\n"
                + "and #SECODE http://lurkmore.ru/ебать-колотить code:   \n"
                + "aaaa \n"
                + "xxx \n"
                + ":code :noparse: \n"
                + "and //THIRD  \n"
                + "  CROSSED^H    and again  \n                            ^бля^ *xx5485685#%#^kl^*?xxx  ** ff ** zoo"
                +"PASTE-222 SHARE-11 >>>54  \n"
                + " fuck da >>2 >>1001  fuck >>4646747 >>3 yo *zxz*v c"
                + " <b>v</b> %%ANAL RAPE%%").setMode(AppMode.PASTE)
                .setPasteUrl("https://mercury.it.ru:7023/paste")
                .setShareUrl("https://mercury.it.ru:7023/share").parseAll().get() + "}");


    }

    public static KabaMarkupParser getInstance() {

        return new KabaMarkupParser();
    }
}
