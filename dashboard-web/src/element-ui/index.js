import Vue from 'vue'
import 'element-ui/lib/theme-chalk/index.css';
import {
    Menu,
    MenuItem,
    MenuItemGroup,
    Submenu,
    Input,
    Icon,
    Button,
    Checkbox,
    Message,
    MessageBox,
    Popover,
    Dialog,
    Form,
    FormItem,
    Table,
    TableColumn,
    Pagination

} from 'element-ui';

Vue.use(Menu)
Vue.use(MenuItem)
Vue.use(MenuItemGroup)
Vue.use(Submenu)
Vue.use(Input)
Vue.use(Icon)
Vue.use(Button)
Vue.use(Checkbox)
Vue.use(Popover)
Vue.use(Dialog)
Vue.use(Form)
Vue.use(FormItem)
Vue.use(Table)
Vue.use(TableColumn)
Vue.use(Pagination)

Vue.prototype.$message = Message
Vue.prototype.$msgbox = MessageBox



