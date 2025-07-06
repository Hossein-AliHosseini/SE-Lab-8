# پیمایشگر گراف

## بخش اول - پیاده‌سازی الگوی Adapter

### زیربخش ۱ - انتخاب نوع Adapter

در این پروژه، شیوه Object Scope برای پیاده‌سازی Adapter انتخاب شده است. علت اصلی این انتخاب، ترکیبی بودن ساختار کلاس GraphAdapter با SparseMultigraph از کتابخانه خارجی بود. به این صورت که یک نمونه از SparseMultigraph به صورت داخلی در GraphAdapter نگهداری می‌شود تا متدهای رابط Graph را فراهم کند. این ساختار باعث افزایش انعطاف‌پذیری، امکان استفاده مجدد و جدا کردن مسئولیت‌ها می‌شود. همچنین، چندین Adapter می‌توانند به یک نمونه مشترک مراجعه کنند که این موضوع قابلیت استفاده مجدد را بالا می‌برد. از سوی دیگر، اگر رویکرد Class Scope انتخاب می‌شد نیازمند ارث‌بری مستقیم از SparseMultigraph بود که انعطاف را کم و وابستگی را زیاد می‌کرد و جایگزینی کتابخانه پایه در آینده دشوار یا حتی نشدنی می‌شد.

### زیربخش ۲ - شیوه پیاده‌سازی الگو

روند انجام کار به این ترتیب است: ابتدا یک رابط به نام Graph<V> تعریف شده که شامل متدهایی از جمله addVertex، addEdge و getNeighbors است. سپس کلاس GraphAdapter با پیاده‌سازی این رابط و استفاده داخلی از SparseMultigraph، رابط Graph را عملیاتی کرده است. هر بار که در کلاینت MainWithAdapter از گراف استفاده می‌شود، در واقع ارتباط فقط با Adapter انجام می‌شود و پیاده‌سازی داخلی SparseMultigraph مخفی باقی می‌ماند. این کار، پیچیدگی ارتباط با کتابخانه خارجی را برای کد کلاینت پنهان کرده و امکان عملیات پیمایش مانند BFS و DFS را با واسطه Adapter مهیا می‌سازد.

## بخش دوم - تعویض کتابخانه

### زیربخش ۱

در زیر، پیاده‌سازی جدید با تغییر کتابخانه آورده شده است:

```java
package org.example.LibraryChange.Graph;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GraphAdapter<V> implements Graph<V> {
  private final SimpleDirectedGraph<V, String> graph;
  private int edgeCount = 0;

  public GraphAdapter() {
this.graph = new SimpleDirectedGraph<>(String.class);
  }

  @Override
  public void addVertex(V vertex) {
graph.addVertex(vertex);
  }

  @Override
  public void addEdge(String name, V source, V destination) {
if (graph.containsEdge(name)) {
throw new IllegalArgumentException("Edge with name " + name + " already exists");
}
if (name == null) {
name = "Edge" + edgeCount++;
}
graph.addEdge(source, destination, name);
  }

  @Override
  public List<V> getNeighbors(V vertex) {
Set<String> outgoingEdges = graph.outgoingEdgesOf(vertex);
Set<String> incomingEdges = graph.incomingEdgesOf(vertex);
List<V> neighbors = new ArrayList<>();
for (String edge : outgoingEdges) {
neighbors.add(graph.getEdgeTarget(edge));
}
for (String edge : incomingEdges) {
neighbors.add(graph.getEdgeSource(edge));
}
return neighbors;
  }
}
```

همانطور که مشخص است، تنها جایی که تغییر نیاز پیدا می‌کند، کلاس GraphAdapter است. نسخه جدید این کلاس از JGraphT استفاده می‌کند و همه عملکردهای لازم را در اختیار می‌گذارد. این رویکرد دقیقا هدف الگوی Adapter را محقق می‌کند.

### زیربخش ۲

برای جایگزینی کتابخانه JUNG با JGraphT لازم است داده‌ها و توابع متناظر به صورت سازگار بازنویسی شوند. در پیاده‌سازی جدید:

#### نکات کلیدی:
1. **ساختار گراف**:
- استفاده از `SimpleDirectedGraph` جایگزین `SparseMultigraph` شده است.
- نوع لبه‌ها به صورت `String` تعریف گردیده است.

2. **مدیریت یال‌ها**:
- در `JGraphT`، هر لبه باید شناسه منحصر بفرد (`String`) داشته باشد؛ این نام‌ها یا دستی تعیین شده‌اند، یا به صورت خودکار ساخته می‌شوند.

3. **بازیابی همسایه‌ها**:
- یال‌های متصل با `outgoingEdgesOf` و `incomingEdgesOf` استخراج و سپس به لیست همسایه‌ها تبدیل می‌شوند.

## بخش سوم - بررسی و تحلیل الگوی Strategy

### سوال ۱: دلیل منطقی بودن استفاده از این الگو در پروژه

دلیل مناسب بودن الگوی Strategy این است که برای پیمایش گراف، گزینه‌های مختلفی مثل DFS و BFS وجود دارد و ممکن است بعداً نیاز به افزودن روش‌های جدید باشد. داشتن یک interface مشترک، امکان تعریف کلاس‌های متفاوت برای هر الگوریتم پیمایش را فراهم می‌سازد. برای مثال، interface زیر تعریف می‌شود:

```java
public interface Traverser {
  List<Integer> traverse(Integer startVertex);
}
```

### توضیحات تکمیلی درباره مزایای Strategy

در این پروژه، الگوی Strategy دارای مزایای متعددی است، از جمله:
 
- **امکان تعویض آسان الگوریتم**  
می‌توان بدون نیاز به تغییرات اساسی در ساختار برنامه، هر زمان که لازم باشد نوع پیمایش را عوض کرد. کافی است نمونه‌ای از کلاس موردنظر (مانند BFS یا DFS) ساخته و جایگزین نمونه قبلی شود.

- **Encapsulation**  
هر الگوریتم پیمایش به صورت جداگانه در کلاس مخصوص خود پیاده‌سازی می‌شود، و کاربر صرفاً با رابط مشترک کار می‌کند. بنابراین، جزییات درون هر الگوریتم از دید مصرف‌کننده مخفی می‌ماند و فقط متد `traverse` فراخوانی می‌شود.

- **Extensibility**  
اگر نیاز به افزودن الگوریتم پیمایشی جدیدی پدید آمد، کافی است یک کلاس جدید با پیاده‌سازی رابط Traverser اضافه شود. استفاده از آن نیز همانند سایر الگوریتم‌ها بدون تغییر ساختار پروژه امکان‌پذیر است.

- **Runtime Flexibility**  
انتخاب الگوریتم پیمایش در زمان اجرا امکان‌پذیر است و می‌توان با توجه به شرایط مختلف، استراتژی مناسب را برگزید یا حتی به صورت داینامیک تغییر داد.

برای نمونه، اگر بخواهیم یک روش پیمایش جدید به پروژه اضافه کنیم، تنها همین مقدار کد اضافه خواهد شد:

```java
class NewGraphTraverser implements Traverser {
  private Graph graph;

  public NewGraphTraverser(Graph graph) {
this.graph = graph;
  }

  @Override
  public List<Integer> traverse(Integer startVertex) {
الگوریتم نمونه فقط برای مثال
List<Integer> path = new ArrayList<>();
path.add(startVertex);

for (int i = 0; i < graph.getVertexCount(); i++) {
if (i != startVertex && graph.isAdjacent(startVertex, i)) {
path.add(i);
}
}
return path;
  }
}
```

// و در نهایت برای استفاده از آن داریم :
```java
Traverser newGraphTraverser = new NewGraphTraverser(graph);
List<Integer> newPath = newGraphTraverser.traverse(1);
System.out.println("Graph-New From node 1 is : " + newPath);
```
