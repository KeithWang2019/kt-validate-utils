# 积木 KtValidateUtils - 轻量级注解驱动对象校验框架

<img src="https://brick.cangsg.com/logo.png" alt="" width="120" height="120">

KtValidateUtils 是一套 **无依赖、高可扩展、嵌套友好** 的 Java 对象校验框架，采用「注解声明规则 + 处理器实现逻辑」的设计模式，专为复杂嵌套对象、集合 / 数组校验场景优化，支持数据实时修正、跨层级依赖校验与精准错误定位，可无缝对接接口入参校验、数据存储校验等业务场景。

## 核心特性

1. **零依赖轻量化**：仅依赖 JDK 8+，无第三方依赖，打包后体积不足 100KB，接入成本极低。
2. **注解驱动开发**：通过注解快速声明校验规则，替代冗余 if-else 代码，开发效率提升 50%+。
3. **复杂结构适配**：自动递归校验嵌套对象、List / 数组、Map，无需手动触发，支持父类字段校验。
4. **数据修正能力**：通过 `setOneself()` 在校验过程中动态修正字段值（如去空格、默认值填充）。
5. **层级关联校验**：通过 `getPreconditionValue()` 跨层级获取父对象字段值，实现依赖校验（如子字段 ≤ 父字段）。
6. **精准错误定位**：返回完整嵌套字段路径（如 user.phones[0].number），快速定位问题字段。
7. **高度可扩展**：支持自定义校验注解与处理器，适配任意复杂业务校验场景（如格式校验、数据库联动校验）。
8. **性能优化**：ThreadLocal 缓存处理器实例，反射操作优化，复杂对象校验性能优于同类轻量框架。
9. **安全防护**：默认限制 20 层嵌套深度，避免循环引用导致的栈溢出。


## 快速开始

### 1. 安装
```xml
<dependency>
        <groupId>com.cangsg.brick</groupId>
        <artifactId>kt-validate-utils</artifactId>
        <version>1.0.0</version>
</dependency>
```

### 2. 基础用法
#### 步骤 1：定义 DTO 并添加校验注解
```java
import com.cangsg.brick.kt.annotation.KtValidateNotEmpty;
import com.cangsg.brick.kt.annotation.KtValidateStringBound;
import com.cangsg.brick.kt.annotation.KtValidateCustom;
import java.util.List;

public class UserDTO {
    // 非空校验 + 字符串长度2-20位
    @KtValidateNotEmpty(message = "用户名不能为空")
    @KtValidateStringBound(minLength = 2, maxLength = 20, message = "用户名长度必须在2-20之间")
    private String username;

    // 非空校验（嵌套对象）
    @KtValidateNotEmpty(message = "地址信息不能为空")
    private AddressDTO address;

    // 非空校验（集合）+ 自定义手机号格式校验
    @KtValidateNotEmpty(message = "手机号列表不能为空")
    private List<PhoneDTO> phones;

    // 父字段：用于子字段依赖校验
    private Integer maxAge = 30;

    // Getter + Setter
}

public class AddressDTO {
    // 字符串长度最大100位
    @KtValidateStringBound(maxLength = 100, message = "详细地址长度不能超过100字符")
    private String detail;

    // Getter + Setter
}

public class PhoneDTO {
    // 自定义手机号格式校验
    @KtValidateCustom(handleBy = PhoneFormatValidator.class, message = "手机号格式错误，需为11位有效数字")
    private String number;

    // 自定义年龄依赖校验（需小于父对象maxAge）
    @KtValidateCustom(handleBy = AgeDependencyValidator.class, message = "年龄不能超过30岁")
    private Integer age;

    // Getter + Setter
}

```

#### 步骤 2：实现自定义处理器（可选）
```java
// 手机号格式校验处理器
public class PhoneFormatValidator extends KtValidator<KtValidateCustom, String> {
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    @Override
    public KtValidateResult<?> call(String val, KtValidateCustom annotation) {
        if (val == null || !val.matches(PHONE_REGEX)) {
            return KtValidateResult.build(annotation.message(), getFieldPath());
        }
        return null;
    }
}

// 年龄依赖校验处理器（跨层级获取父对象字段）
public class AgeDependencyValidator extends KtValidator<KtValidateCustom, Integer> {
    @Override
    public KtValidateResult<?> call(Integer val, KtValidateCustom annotation) {
        if (val == null) return null;
        // 跨层级获取父对象（UserDTO）的 maxAge 字段值（distance=1 表示直接父对象）
        Integer parentMaxAge = getPreconditionValue(1, "maxAge");
        if (val > parentMaxAge) {
            return KtValidateResult.build(annotation.message(), getFieldPath());
        }
        return null;
    }
}
```


#### 步骤 3：执行校验并处理结果
```java
import com.cangsg.brick.kt.KtValidateUtils;
import com.cangsg.brick.kt.entry.KtValidateResult;
import java.util.Collections;

public class ValidateDemo {
    public static void main(String[] args) {
        // 构造测试数据（含无效手机号和超龄数据）
        UserDTO user = new UserDTO();
        user.setUsername("  张三  "); // 带空格，将通过处理器自动去空格
        user.setAddress(new AddressDTO());
        user.getAddress().setDetail("北京市朝阳区建国路88号");
        
        PhoneDTO phone = new PhoneDTO();
        phone.setNumber("12345"); // 无效手机号
        phone.setAge(35); // 超过 maxAge=30
        user.setPhones(Collections.singletonList(phone));

        // 执行校验
        KtValidateResult<?> result = KtValidateUtils.valid(user);

        // 处理校验结果
        if (result.hasError()) {
            System.out.println("校验失败：" + result.getErrorMsg());
            System.out.println("错误字段：" + buildFieldPath(result.getFieldPath()));
            // 输出：
            // 校验失败：年龄不能超过30岁
            // 错误字段：user.phones[0].age
        } else {
            System.out.println("校验通过，修正后的用户名：" + user.getUsername()); // 输出：张三（已去空格）
        }
    }

    // 辅助方法：将路径列表转换为友好字符串
    private static String buildFieldPath(List<Object> pathList) {
        StringBuilder sb = new StringBuilder();
        for (Object path : pathList) {
            if (path instanceof Integer) {
                sb.append("[").append(path).append("]");
            } else {
                if (sb.length() > 0) sb.append(".");
                sb.append(path);
            }
        }
        return sb.toString();
    }
}
```

## 三、核心组件说明
### 1. 内置校验注解
| 注解名称 | 作用 | 核心属性 |
|----------|----------|--------|
| `@KtValidateNotEmpty` | 非空校验 | message：失败提示信息（支持 String、引用类型、集合 / 数组非空） |
| `@KtValidateStringBound` | 字符串长度校验 | minLength（默认 0）、maxLength（默认 50）、message：失败提示信息 |
| `@KtValidateCustom` | 自定义校验 | handleBy：指定自定义处理器类、message：失败提示信息 |

### 2. 核心工具类 `KtValidateUtils`
对外提供统一校验入口（无状态，无需实例化）：
- `static <T> KtValidateResult<?> valid(T obj)`：校验单个对象（支持任意引用类型）
- `static <T> KtValidateResult<?> valid(List<T> list)`：校验集合（自动处理列表内元素）

### 3. 校验结果 `KtValidateResult<T>`
封装校验结果，提供友好的反馈能力：
- `boolean hasError()`：校验单个对象（支持任意引用类型）
- `String getErrorMsg()`：校验集合（自动处理列表内元素）
- `List<Object> getFieldPath()`：获取错误字段的完整嵌套路径
- `static <T> KtValidateResult<T> build(String errorMsg, List<Object> fieldPath)`：快速创建结果对象

### 4. 处理器抽象基类 `KtValidator<A extends Annotation, T>`
所有校验处理器的父类，封装通用能力：
- `U getPreconditionValue(int distance, String name)`：跨层级获取父对象字段值
- `void setOneself(T val)`：动态修改当前字段值（数据修正）

## 四、高级功能详解
### 1. 数据修正：`setOneself` 方法
在校验过程中动态修改字段值，实现「校验 + 修正一体化」，无需额外编写数据处理逻辑。
#### 典型场景 1：字符串自动去空格
```java
// 自定义处理器：去空格后校验长度
public class TrimStringLengthValidator extends KtValidator<KtValidateTrimLength, String> {
    @Override
    public KtValidateResult<?> call(String val, KtValidateTrimLength annotation) {
        if (val != null) {
            String trimmedVal = val.trim();
            setOneself(trimmedVal); // 更新字段值为去空格后的值
            if (trimmedVal.length() < annotation.minLength() || trimmedVal.length() > annotation.maxLength()) {
                return KtValidateResult.build(annotation.message(), getFieldPath());
            }
        }
        return null;
    }
}
```
#### 典型场景 2：空值默认填充
```java
// 自定义处理器：null 填充默认值
public class DefaultValueValidator extends KtValidator<KtValidateDefault, String> {
    @Override
    public KtValidateResult<?> call(String val, KtValidateDefault annotation) {
        if (val == null) {
            setOneself(annotation.defaultValue()); // 填充注解指定的默认值
        }
        return null;
    }
}

// 配套注解定义
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@KtValidateHandler(handleBy = DefaultValueValidator.class)
public @interface KtValidateDefault {
    String defaultValue() default "";
    String message() default "";
}
```
#### 使用约束：
- 字段需遵循 JavaBean 规范，提供公共 Setter 方法
- 传入值类型需与字段类型一致（或支持自动转型）
- 仅修改内存中当前对象实例，不影响外部存储
### 2. 层级关联校验：跨层级依赖校验
通过 `getPreconditionValue(int distance, String name)` 方法，跨层级获取父对象字段值，实现依赖校验。
#### 方法参数说明：
- `distance`：向上追溯层级数（0 = 当前对象， 1 = 直接父对象，2 = 祖父对象，以此类推）
- `name`：目标字段名（需提供 Getter 方法）
- 返回值：父对象字段值（泛型自动适配类型）
#### 复杂场景示例：多层级依赖
```java
// 孙字段校验：需包含祖父对象的 company 名称
public class GrandparentDependencyValidator extends KtValidator<KtValidateCustom, String> {
    @Override
    public KtValidateResult<?> call(String val, KtValidateCustom annotation) {
        // 获取祖父对象的 company 字段值（distance=2）
        String grandparentCompany = getPreconditionValue(2, "company");
        if (val == null || !val.contains(grandparentCompany)) {
            return KtValidateResult.build(annotation.message(), getFieldPath());
        }
        return null;
    }
}
```
## 五、自定义校验扩展
当内置注解无法满足业务需求时，可通过「自定义注解 + 处理器」快速扩展，步骤如下：
### 步骤 1：定义自定义校验注解（可选，也可直接使用 @KtValidateCustom）
```java
import com.cangsg.brick.kt.entry.KtValidateHandler;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@KtValidateHandler(handleBy = IdCardValidator.class) // 绑定处理器
public @interface KtValidateIdCard {
    String message() default "身份证号格式错误"; // 默认提示信息
}
```
### 步骤 2：实现自定义处理器
```java
import com.cangsg.brick.kt.annotation.KtValidateIdCard;
import com.cangsg.brick.kt.entry.KtValidateResult;
import com.cangsg.brick.kt.entry.KtValidator;

// 身份证号格式校验处理器
public class IdCardValidator extends KtValidator<KtValidateIdCard, String> {
    private static final String ID_CARD_REGEX = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";

    @Override
    public KtValidateResult<?> call(String val, KtValidateIdCard annotation) {
        if (val == null || !val.matches(ID_CARD_REGEX)) {
            return KtValidateResult.build(annotation.message(), getFieldPath());
        }
        return null;
    }
}
```
### 步骤 3：使用自定义注解
```java
public class UserDTO {
    @KtValidateIdCard(message = "身份证号格式不正确，请输入18位有效号码")
    private String idCard;

    // Getter + Setter
}
```
## 六、同类工具对比
| 对比维度 | KtValidateUtils | Hibernate Validator（JSR-380） | Spring Validation（基于 Hibernate） | Apache Commons Validator |
|----------|----------|--------|--------|--------|
| 核心定位 | 轻量级嵌套校验，支持数据修正与层级关联 | 工业级标准校验框架，JSR-380 实现 | 整合 Hibernate，适配 Spring 生态 | 纯 API 调用的校验工具类（无注解） |
| 依赖要求 | 无第三方依赖（JDK 8+） | 需依赖 JSR-380 API + Hibernate 核心包 | 需依赖 Spring 核心 + Hibernate Validator | 需依赖 Commons 系列基础包 |
| 嵌套校验 | 自动递归处理，无需手动触发 | 需添加 @Valid 触发 | 需添加 @Valid 触发 | 需手动递归处理，无自动支持 |
| 核心特色 | 数据修正、跨层级依赖校验、精准路径定位 | 标准注解丰富、支持分组 / 条件校验、EL 表达式 | Spring 生态无缝整合、全局异常处理适配 | 预定义规则多、支持 XML 配置 |
| 易用性 | 学习成本低，无配置依赖 | 需学习 JSR-380 规范，配置项多 | Spring 项目零配置，非 Spring 适配成本高 | 纯代码调用，冗余度高 |
| 适用场景 | 轻量级项目、复杂嵌套对象、数据修正需求 | 企业级项目、多团队协作、标准规范场景 | Spring Boot/Spring Cloud 项目 | 简单字段校验、无注解需求场景 |
| 性能表现 | 轻量无冗余，反射优化 + ThreadLocal 缓存，性能优异 | 功能全面但冗余较多，启动与校验耗时略高 | 依赖 Spring 上下文，性能与 Hibernate 接近 | 纯 API 调用，简单场景性能好，复杂场景需手动优化 |

- 若项目是 轻量级 / 无框架依赖，或需要 数据修正、跨层级校验，优先选 KtValidateUtils；

## License

[MIT](LICENSE)
