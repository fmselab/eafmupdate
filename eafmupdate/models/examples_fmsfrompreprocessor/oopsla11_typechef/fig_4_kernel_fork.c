static void rt_mutex_init_task(struct task_struct *p) {
#ifdef CONFIG_DEBUG_SPINLOCK
do { static struct lock_class_key __key;
__raw_spin_lock_init((&p->pi_lock), "&p
4 ->pi_lock", &__key); } while (0)
5 #else
6 do { *(&p->pi_lock) = (raw_spinlock_t) { .raw_lock =
7 #ifdef CONFIG_SMP
8 { 0 }
9 #else
10 { }
11 #endif
12 ,
13 #ifdef CONFIG_DEBUG_LOCK_ALLOC
14 .dep_map = { .name = "&p->pi_lock" }
15 #endif
16 }; } while (0)
17 #endif
18 ;
19 #ifdef CONFIG_RT_MUTEXES
20 plist_head_init_raw(&p->pi_waiters, &p->pi_lock);
21 p->pi_blocked_on = ((void *)0);
22 #endif
23 }
