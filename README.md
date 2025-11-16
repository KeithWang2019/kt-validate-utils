# 积木 KtValidateUtils - 轻量级注解驱动对象校验框架

<img src="https://brick.cangsg.com/logo.png" alt="" width="120" height="120">

KtValidateUtils 是一套 **无依赖、高可扩展、嵌套友好** 的 Java 对象校验框架，采用「注解声明规则 + 处理器实现逻辑」的设计模式，专为复杂嵌套对象、集合 / 数组校验场景优化，支持数据实时修正、跨层级依赖校验与精准错误定位，可无缝对接接口入参校验、数据存储校验等业务场景。

## 核心特性

1. **零依赖轻量化**：仅依赖 JDK 8+，无第三方依赖，打包后体积不足 100KB，接入成本极低。
2. 注解驱动开发：通过注解快速声明校验规则，替代冗余 if-else 代码，开发效率提升 50%+。
3. 复杂结构适配：自动递归校验嵌套对象、List / 数组、Map，无需手动触发，支持父类字段校验。
4. 数据修正能力：通过 setOneself() 在校验过程中动态修正字段值（如去空格、默认值填充）。
5. 层级关联校验：通过 getPreconditionValue() 跨层级获取父对象字段值，实现依赖校验（如子字段 ≤ 父字段）。
6. 精准错误定位：返回完整嵌套字段路径（如 user.phones[0].number），快速定位问题字段。
7. 高度可扩展：支持自定义校验注解与处理器，适配任意复杂业务校验场景（如格式校验、数据库联动校验）。
8. 性能优化：ThreadLocal 缓存处理器实例，反射操作优化，复杂对象校验性能优于同类轻量框架。
9. 安全防护：默认限制 20 层嵌套深度，避免循环引用导致的栈溢出。


## 快速开始

### 0. 安装
```xml
<dependency>
        <groupId>com.cangsg.brick</groupId>
        <artifactId>kt-properties-utils</artifactId>
        <version>1.0.0</version>
</dependency>
```

### 1. 依赖引入
（若项目已集成相关包，无需额外引入；否则需确保以下核心类在类路径中）

com.cangsg.brick.kt.KtPropertiesUtils

com.cangsg.brick.kt.KtPropertiesUtilscom.cangsg.brick.kt.api.*（含 KtPropertiesChain、BeanUtils 等）


### 2. 基础用法
#### 2.1 单个对象拷贝（同类型/不同类型）
```java
// 源对象（DTO）
UserDTO source = new UserDTO("张三", 25, new AddressDTO("北京"));

// 1. 不同类型映射（DTO→PO）
UserPO target = KtPropertiesUtils.copyProperties(source, UserPO.class)
        .mapType(AddressDTO.class, AddressPO.class) // 嵌套对象类型映射
        .build();

// 2. 同类型深拷贝（复用目标对象）
UserDTO copy = new UserDTO();
copy = KtPropertiesUtils.copyProperties(source, copy).build();

// 验证深拷贝：修改目标对象嵌套属性，源对象不受影响
target.getAddress().setCity("上海");
System.out.println(source.getAddress().getCity()); // 输出：北京
```

#### 2.2 List 集合拷贝
```java
List<UserDTO> sourceList = Arrays.asList(
        new UserDTO("张三", 25, new AddressDTO("北京")),
        new UserDTO("李四", 30, new AddressDTO("上海"))
);

// List<DTO> → List<PO>
List<UserPO> targetList = KtPropertiesUtils.copyProperties(sourceList, UserPO.class)
        .mapType(AddressDTO.class, AddressPO.class)
        .build();
```


#### 2.3 Map 拷贝（String 键类型）
```java
Map<String, UserDTO> sourceMap = new HashMap<>();
sourceMap.put("user1", new UserDTO("张三", 25, new AddressDTO("北京")));

// Map<String, DTO> → Map<String, PO>
Map<String, UserPO> targetMap = KtPropertiesUtils.copyProperties(sourceMap, UserPO.class)
        .mapType(AddressDTO.class, AddressPO.class)
        .build();
```

#### 2.4 自定义值转换
```java
// 自定义字段转换（如状态码映射、日期格式化）
UserDTO source = new UserDTO("张三", 25, new AddressDTO("北京"));
UserPO target = KtPropertiesUtils.copyProperties(source, UserPO.class)
        .mapValue((destType, propertyName, sourceValue) -> {
            // destType：目标字段类型全类名，propertyName：字段名，sourceValue：源字段值
            if ("java.lang.String".equals(destType) && "city".equals(propertyName)) {
                return "中国-" + sourceValue; // 地址前缀拼接
            }
            if ("java.lang.Integer".equals(destType) && "age".equals(propertyName)) {
                return ((Integer) sourceValue) + 1; // 年龄+1
            }
            return sourceValue; // 无转换则返回原值
        })
        .build();

System.out.println(target.getAddress().getCity()); // 输出：中国-北京
System.out.println(target.getAge()); // 输出：26
```
## 详细 API
### 1. 核心入口方法（KtPropertiesUtils）
| 方法签名 | 功能描述 | 返回值 |
|----------|----------|--------|
| `copyProperties(T source, V target)` | 源对象 → 已实例化目标对象 | KtPropertiesChain<T, V> |
| `copyProperties(T source, Class<V> clazz)` | 源对象 → 新实例化目标对象（无参构造） | KtPropertiesChain<T, V> |
| `copyProperties(List<T> source, List<V> target)` | 源 List → 已实例化目标 List | KtListPropertiesChain<T, V> |
| `copyProperties(List<T> source, Class<V> clazz)` | 源 List → 新 List（元素为目标类实例） | KtListPropertiesChain<T, V> |
| `copyProperties(Map<String,T> source, Map<String,V> target)` | 源 Map → 已实例化目标 Map | KtMapPropertiesChain<T, V> |
| `copyProperties(Map<String,T> source, Class<V> clazz)` | 源 Map → 新 Map（value 为目标类实例） | KtMapPropertiesChain<T, V> |

### 2. 链式配置方法
| 方法名 | 功能描述 | 示例 |
|--------|----------|------|
| `mapType(Class<?> sourceType, Class<?> targetType)` | 配置源类型→目标类型的映射（支持嵌套对象） | `.mapType(AddressDTO.class, AddressPO.class)` |
| `mapValue(KtMapValueHandler handler)` | 配置自定义值转换逻辑 | `.mapValue((destType, name, value) -> {...})` |
| `build()` | 执行拷贝并返回目标对象 | `.build()` |

## 注意事项
### 1. 前提条件
- 目标类必须提供 **公共无参构造**（实例化需要）
- 拷贝的属性必须包含 **标准 getter 方法**（源对象读取值）和 **标准 setter 方法**（目标对象赋值）
- 嵌套对象的类型映射需通过 `mapType` 显式配置（否则无法自动转换类型）

### 2. 特殊类型处理
- 枚举类型：通过枚举名（name()）匹配，与枚举顺序无关
- 数组类型：支持基本类型数组（如 int[]）和对象数组（如 User[]），自动拷贝元素
- 循环引用：默认限制最大嵌套深度 20 层，若需支持合法深嵌套（如树形结构），可修改 `KtPropertiesChain` 中 `depth` 限制值

### 3. 分布式场景最佳实践
- 核心链路建议添加拷贝耗时监控（如 Prometheus 埋点），确保无性能异常
- 大批量数据拷贝（单次 >1w 条）建议异步执行，避免占用核心线程池
- 热点类（如高频 DTO/PO）的拷贝无需额外优化，缓存命中率接近 100%

## 性能说明
| 工具 | 热启动单次拷贝耗时 | 核心开销来源 |
|----------|----------|----------|
| MapStruct | 0.1 微秒（μs） | 原生方法调用（几乎无开销） |
| Orika | 0.3~0.5 微秒（μs） | 动态生成字节码调用 |
| KtPropertiesUtils | 1~2 微秒（μs） | 反射调用 getter/setter + 递归拷贝 |

但在大型分布式项目的实际运行中，KtPropertiesUtils 表现出与 Orika/MapStruct 相当的性能水平，核心原因：
1. 拷贝操作在分布式链路中耗时占比极低（通常 <1%），被网络、数据库等开销掩盖
2. 反射缓存优化后，热启动场景下单次拷贝耗时仅 1~2 微秒，与 Orika/MapStruct 的理论差距在实际场景中可忽略
3. 集群冗余资源完全抵消拷贝工具的少量额外开销，无实际性能损失

## 兼容性
- JDK 版本：支持 Java 8 及以上
- 框架兼容：无冲突，可与 Spring Boot、Dubbo 等分布式框架无缝集成
- 第三方类兼容：支持无修改权限的第三方类拷贝（需满足 getter/setter 规范）

## 问题排查
### 常见异常及解决方案
| 异常类型 | 可能原因 | 解决方案 |
|----------|----------|----------|
| NoSuchMethodException | 目标类无公共无参构造 | 为目标类添加无参构造（public 修饰） |
| IllegalAccessException | getter/setter 非 public 权限 | 改为 public 权限，或确保安全管理器允许 setAccessible(true) |
| NullPointerException | 源对象为 null，或字段名不匹配 | 校验源对象非 null；确保源/目标字段名一致（大小写敏感） |
| RuntimeException（深度>20） | 存在循环引用 | 检查对象引用关系，或调整 `KtPropertiesChain` 中最大深度限制 |

## 与主流工具对比
| 特性 | KtPropertiesUtils | Orika | MapStruct |
|------|-------------------|-------|-----------|
| 底层实现 | 反射+缓存优化 | 运行时字节码生成 | 编译时代码生成 |
| 分布式适配 | 无依赖、轻量、低冲突 | 需引入依赖，可能冲突 | 需引入依赖+编译插件 |
| 实际性能（分布式场景） | 无明显损失 | 略优 | 理论最优 |
| 易用性 | 链式 API，零配置起步 | 需配置 MapperFacade | 需定义映射接口 |
| 灵活性 | 支持动态映射+自定义转换 | 支持复杂映射 | 编译时固定映射 |
| 侵入性 | 无 | 无 | 需添加注解 |

### 选型建议
- 若项目已使用 KtPropertiesUtils 且无性能问题：**继续使用**，无需盲目替换
- 核心链路大批量拷贝场景：可局部引入 MapStruct 补充
- 需动态映射或第三方类拷贝：KtPropertiesUtils 或 Orika 均可
- 重视编译时校验：优先 MapStruct

### 理念推广：属性复制工具 [KtPropertiesUtils](https://github.com/KeithWang2019/kt-properties-utils) 工具，特殊理念，请详读内容，了解为什么允许性能损失，并且正式上线前请做好相关测试。

## License

[MIT](LICENSE)
