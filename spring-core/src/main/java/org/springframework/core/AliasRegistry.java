/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core;

/**
 * Common interface for managing aliases. Serves as super-interface for
 * 管理别名的公共接口。
 * 可以对别名进行增删改查等操作。
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}.
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 */
public interface AliasRegistry {

	/**
	 * Given a name, register an alias for it.
	 * 给定一个名称,为它注册一个别名
	 * @param name the canonical name 一个规范的名称
	 * @param alias the alias to be registered 需要注册的别名
	 * @throws IllegalStateException if the alias is already in use 如果别名已经存在,抛出IllegalStateException异常
	 * and may not be overridden
	 */
	void registerAlias(String name, String alias);

	/**
	 * Remove the specified alias from this registry.
	 * 从注册表中删除指定的别名
	 * @param alias the alias to remove 要删除的别名
	 * @throws IllegalStateException if no such alias was found 如果找不到这样的别名,抛出IllegalStateException异常
	 */
	void removeAlias(String alias);

	/**
	 * Determine whether this given name is defines as an alias
	 * 确定给定的名称是否为别名。
	 * (as opposed to the name of an actually registered component 而不是实际注册的组件的名称).
	 * <s>这个地方从备注中得到的信息有点疑惑，如果给出的名称没有被注册为别名(不是实际组件的名称,也不是别名)，会返回什么。</s>
	 * @param name the name to check 要检查的名称
	 * @return whether the given name is an alias 给定的名称是否是别名
	 */
	boolean isAlias(String name);

	/**
	 * Return the aliases for the given name, if defined.
	 * 如果给定名称的别名已定义，则返回给定名称的别名。
	 * @param name the name to check for aliases 要检查别名的名称
	 * @return the aliases, or an empty array if none 返回别名,如果没有返回空
	 */
	String[] getAliases(String name);

}
