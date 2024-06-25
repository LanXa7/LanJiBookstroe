// import {createRouter, createWebHistory} from 'vue-router'
// import {unauthorized} from "@/net";
//
// const router = createRouter({
//     history: createWebHistory(import.meta.env.BASE_URL),
//     routes: [
//         {
//             path: '/',
//             name: 'welcome',
//             component: () => import('@/views/WelcomeView.vue'),
//             children: [
//                 {
//                     path: '',
//                     name: 'welcome-login',
//                     component: () => import('@/views/welcome/LoginPage.vue')
//                 }, {
//                     path: 'register',
//                     name: 'welcome-register',
//                     component: () => import('@/views/welcome/RegisterPage.vue')
//                 }, {
//                     path: 'forget',
//                     name: 'welcome-forget',
//                     component: () => import('@/views/welcome/ForgetPage.vue')
//                 }
//             ]
//         }, {
//             path: '/index',
//             name: 'index',
//             component: () => import('@/views/IndexView.vue'),
//             children: [
//                 {
//                     path: 'user-setting',
//                     name: 'user-setting',
//                     component: () => import('@/views/person/UserSetting.vue')
//                 }
//             ]
//         }
//
//
// router.beforeEach((to, from, next) => {
//     const isUnauthorized = unauthorized()
//     if (to.name.startsWith('welcome') && !isUnauthorized) {
//         next('/index')
//     } else if (to.fullPath.startsWith('/index') && isUnauthorized) {
//         next('/')
//     } else {
//         next()
//     }
//
//
//     if (to.name.startsWith('index') && !isUnauthorized) {
//         next('/welcome')
//     } else if (to.fullPath.startsWith('/welcome') && isUnauthorized) {
//         next('/')
//     } else {
//         next()
//     }
//
//     if (to.matched.some(record => record.meta.requiresAuth)) {
//         // 如果用户未登录，且尝试访问需要认证的页面，则重定向到登录页面
//         if (isUnauthorized) {
//             next('/welcome');
//         } else {
//             next();
//         }
//     } else {
//         next(); // 如果不需要认证，则直接放行
//     }
// })
//
// export default router
import {createRouter, createWebHistory} from 'vue-router'
import {unauthorized} from "@/net";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'index',
            component: () => import('@/views/IndexView.vue'),
            children: [
                {
                    path: '',
                    name: 'home',
                    component: () => import('@/views/Home.vue'),
                    meta: {requiresAuth: false} // 首页无需认证
                },
                {
                    path: 'book-manager',
                    name: 'book-manager',
                    component: () => import('@/views/book/BookManager.vue')
                },
                {
                    path: 'type-manager',
                    name: 'type-manager',
                    component: () => import('@/views/book/TypeManager.vue')
                },
                {
                    path: 'data-manager',
                    name: 'data-manager',
                    component: () => import('@/views/book/BookstoreData.vue')
                },
                {
                    path: 'order-manager',
                    name: 'order-manager',
                    component: () => import('@/views/OrderManager.vue')
                },
                {
                    path: 'shopping-cart',
                    name: 'shopping-cart',
                    component: () => import('@/views/Cart.vue'),
                    meta: {requiresAuth: true}
                },
                {
                    path: 'user-setting',
                    name: 'user-setting',
                    component: () => import('@/views/person/UserSetting.vue'),
                    meta: {requiresAuth: true}  // 个人信息页面需要认证
                },
                {
                    path: 'address-setting',
                    name: 'address-setting',
                    component: () => import('@/views/person/AddressSetting.vue'),
                    meta: {requiresAuth: true}  // 个人信息页面需要认证
                },
                {
                    path: 'my-order',
                    name: 'my-order',
                    component: () => import('@/views/person/Order.vue'),
                    meta: {requiresAuth: true}  // 个人信息页面需要认证
                },
                {
                    path: 'account-setting',
                    name: 'account-setting',
                    component: () => import('@/views/person/PrivacySetting.vue'),
                    meta: {requiresAuth: true}  // 个人信息页面需要认证
                }
            ]
        },
        {
            path: '/welcome',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/views/welcome/LoginPage.vue')
                },
                {
                    path: 'register',
                    name: 'welcome-register',
                    component: () => import('@/views/welcome/RegisterPage.vue')
                },
                {
                    path: 'forget',
                    name: 'welcome-forget',
                    component: () => import('@/views/welcome/ForgetPage.vue')
                }
            ]
        },
    ]
})

router.beforeEach((to, from, next) => {
    //console.log(`Navigating to ${to.path}`);
    const isUnauthorized = unauthorized();
    //console.log(`Is Unauthorized: ${isUnauthorized}`);
    if (to.matched.some(record => record.meta.requiresAuth)) {
        //console.log('Route requires auth');
        // 如果用户未登录，且尝试访问需要认证的页面，则重定向到登录页面
        if (isUnauthorized) {
            next('/welcome');
        } else {
            next();
        }
    } else {
        next(); // 如果不需要认证，则直接放行
    }
});

export default router;
