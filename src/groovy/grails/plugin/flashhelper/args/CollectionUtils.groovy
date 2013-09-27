package grails.plugin.flashhelper.args


class CollectionUtils {

    static getListItemSafely(List list, Integer index) {

        if (index < list.size()) {
            list[index]
        }
    }
    
    static asCollection(obj) {

        if (obj == null) {
            return Collections.emptyList()
        }
        obj instanceof Collection ? obj : [obj]
    }
}
